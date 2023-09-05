package info.kgeorgiy.ja.selivanov.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {

    private final static int MAX_QUEUE_SIZE = 100000;
    private final List<Thread> threads;
    private final Queue<Runnable> tasks;

    public ParallelMapperImpl(int threadsCount) {
        if (threadsCount <= 0) {
            throw new RuntimeException("Number of threads should be positive");
        }
        this.threads = new ArrayList<>(threadsCount);
        this.tasks = new ArrayDeque<>();
        for (int i = 0; i < threadsCount; ++i) {
            // :NOTE: worker вынести из цикла
            threads.add(new Thread(worker()));
            threads.get(i).start();
        }
    }

    private Runnable worker() {
        return () -> {
            try {
                while (!Thread.interrupted()) {
                    Runnable runnable;
                    synchronized (tasks) {
                        while (tasks.isEmpty()) {
                            tasks.wait();
                        }
                        runnable = tasks.poll();
                        // :NOTE: redundant
                        tasks.notifyAll();
                    }
                    runnable.run();
                }
            } catch (InterruptedException ignored) {
            } finally {
                Thread.currentThread().interrupt();
            }
        };
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        ResultList<R> result = new ResultList<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            int num = i;
            addTask(() -> result.set(num, f.apply(args.get(num))));
        }
        return result.get();
    }

    private void addTask(Runnable task) throws InterruptedException {
        synchronized (tasks) {
            while (tasks.size() == MAX_QUEUE_SIZE) {
                task.wait();
            }
            tasks.add(task);
            tasks.notifyAll();
        }
    }

    @Override
    public void close() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static class ResultList<E> {
        private final List<E> list;
        private int counter;

        ResultList(int size) {
            this.list = new ArrayList<>(Collections.nCopies(size, null));
        }

        void set(int pos, E data) {
            list.set(pos, data);
            synchronized (this) {
                // :NOTE: ==
                if (++counter >= list.size()) {
                    notify();
                }
            }
        }

        synchronized List<E> get() throws InterruptedException {
            while (counter < list.size()) {
                wait();
            }
            return list;
        }
    }
}
