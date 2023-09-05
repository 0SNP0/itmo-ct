package info.kgeorgiy.ja.selivanov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

public class WebCrawler implements Crawler {

    private final Downloader downloader;
    private final ExecutorService downloaders;
    private final ExecutorService extractors;
    private final int perHost;
    private final Map<String, Host> hosts;

    /**
     * {@link WebCrawler} constructor
     *
     * @param downloader  {@link Downloader}
     * @param downloaders downloaders count
     * @param extractors  extractors count
     * @param perHost     host downloads count
     */
    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
        this.perHost = perHost;
        this.hosts = new ConcurrentHashMap<>();
    }

    @Override
    public Result download(String url, int depth) {
        final Set<String> downloaded = ConcurrentHashMap.newKeySet();
        final Map<String, IOException> errors = new ConcurrentHashMap<>();
        final Set<String> used = ConcurrentHashMap.newKeySet();
        final Phaser phaser = new Phaser(1);
        used.add(url);

        Queue<UrlAndDepth> queue = new ConcurrentLinkedQueue<>();
        queue.add(new UrlAndDepth(url, depth - 1));
        int curDepth = depth;
        while (!queue.isEmpty()) {
            UrlAndDepth current = Objects.requireNonNull(queue.poll());
            if (curDepth > current.depth) {
                phaser.arriveAndAwaitAdvance();
                curDepth--;
            }

            try {
                String hostName = URLUtils.getHost(current.url);
                Host tasks = hosts.computeIfAbsent(hostName, x -> new Host(downloaders, perHost));
                phaser.register();
                tasks.add(() -> {

                    try {
                        Document document = downloader.download(current.url);
                        downloaded.add(current.url);
                        if (current.depth > 0) {
                            phaser.register();
                            extractors.submit(() -> {

                                try {
                                    for (String link : document.extractLinks()) {
                                        if (used.add(link)) {
                                            queue.add(new UrlAndDepth(link, current.depth - 1));
                                        }
                                    }
                                } catch (IOException e) {
                                    System.err.println("Exception while extracting links from document by url: " + current.url);
                                } finally {
                                    phaser.arriveAndDeregister();
                                }
                            });
                        }
                    } catch (IOException e) {
                        errors.put(current.url, e);
                    } finally {
                        phaser.arriveAndDeregister();
                        tasks.runNext();
                    }
                });

            } catch (MalformedURLException e) {
                errors.put(current.url, e);
            }
            if (queue.isEmpty()) {
                phaser.arriveAndAwaitAdvance();
            }
        }

        phaser.arriveAndAwaitAdvance();
        return new Result(new ArrayList<>(downloaded), errors);
    }

    @Override
    public void close() {
        for (ExecutorService pool : List.of(downloaders, extractors)) {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException e) {
                pool.shutdownNow();
            }
        }
    }

    private static class Host {

        private final Queue<Runnable> queue;
        private final ExecutorService downloaderPool;
        private final int perHost;
        private int workingTasks;

        private Host(ExecutorService downloaderPool, int perHost) {
            this.queue = new ConcurrentLinkedDeque<>();
            this.downloaderPool = downloaderPool;
            this.perHost = perHost;
            workingTasks = 0;
        }

        private synchronized void add(Runnable task) {
            if (workingTasks++ < perHost) {
                downloaderPool.submit(task);
            } else {
                queue.add(task);
            }
        }

        private synchronized void runNext() {
            Runnable task = queue.poll();
            if (task == null) {
                workingTasks--;
            } else {
                downloaderPool.submit(task);
            }
        }
    }

    private record UrlAndDepth(String url, int depth) {
    }

    public static void main(String... args) {
        if (args == null || args.length == 0 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("Arguments should be not null");
            return;
        }
        Function<Integer, Integer> intArg = i -> i < args.length ? Integer.parseInt(args[i]) : 1;

        try (Crawler crawler = new WebCrawler(new CachingDownloader(), intArg.apply(1), intArg.apply(2), intArg.apply(3))) {
            crawler.download(args[0], intArg.apply(1));
        } catch (IOException e) {
            System.err.println("Downloader creating error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Parsing argument error: " + e.getMessage());
        }
    }
}
