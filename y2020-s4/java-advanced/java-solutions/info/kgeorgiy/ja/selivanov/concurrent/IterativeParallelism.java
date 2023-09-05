package info.kgeorgiy.ja.selivanov.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IterativeParallelism implements ListIP {
    private final ParallelMapper parallelMapper;

    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    public IterativeParallelism() {
        this(null);
    }

    /**
     * Abstract parallelism function
     *
     * @param list          given values
     * @param threadsNumber number of threads
     * @param mapper        function, whose apply to all elements
     * @param merge         function, whose join all elements
     * @param <E>           values type
     * @param <R>           returned type
     * @return result of applying and joining on list
     * @throws InterruptedException if there is a problem with threads
     */
    private <E, R> R run(List<? extends E> list,
                         int threadsNumber,
                         Function<Stream<? extends E>, ? extends R> mapper,
                         Function<Stream<? extends R>, ? extends R> merge
    ) throws InterruptedException {
        if (threadsNumber <= 0) {
            throw new RuntimeException("Number of threads should be positive");
        }
        List<Stream<? extends E>> splitted = split(list, threadsNumber);
        List<R> results = multiMap(splitted, mapper);
        Objects.requireNonNull(results);
        return merge.apply(results.stream());
    }

    /**
     * Separates values by threads
     *
     * @param list          given values
     * @param threadsNumber number of threads
     * @param <E>           values type
     * @return list of streams for each thread
     */
    private <E> List<Stream<? extends E>> split(List<? extends E> list, int threadsNumber) {
        List<Stream<? extends E>> parts = new ArrayList<>();
        int partSize = list.size() / threadsNumber, rem = list.size() % threadsNumber;
        int begin = 0;
        for (int i = 0; i < threadsNumber; i++) {
            int size = i < rem ? partSize + 1 : partSize;
            if (size == 0) {
                break;
            }
            parts.add(list.subList(begin, begin + size).stream());
            begin += size;
        }
        return parts;
    }

    /**
     * Processes each stream as a separate thread
     *
     * @param values list of streams of values for each thread
     * @param mapper function, whose apply to all elements
     * @param <E>    values type
     * @param <R>    result type
     * @return list of results for each stream of values
     * @throws InterruptedException if there is a problem with threads
     */
    private <E, R> List<R> multiMap(
            List<Stream<? extends E>> values,
            Function<Stream<? extends E>, ? extends R> mapper
    ) throws InterruptedException {
        if (parallelMapper != null) {
            return parallelMapper.map(mapper, values);
        }

        int threads = values.size();
        List<R> result = new ArrayList<>(Collections.nCopies(threads, null));
        List<Thread> tasks = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            final int num = i;
            tasks.add(new Thread(() -> result.set(num, mapper.apply(values.get(num)))));
            tasks.get(num).start();
        }
        InterruptedException exception = null;
        for (Thread thread : tasks) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
        return result;
    }

    @Override
    public String join(int threads, List<?> values) throws InterruptedException {
        return run(values, threads,
                x -> x.map(Object::toString).collect(Collectors.joining()),
                x -> x.collect(Collectors.joining()));
    }

    @Override
    public <T> List<T> filter(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return run(values, threads,
                x -> x.filter(predicate).collect(Collectors.toList()),
                x -> x.flatMap(List::stream).collect(Collectors.toList()));
    }

    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values, Function<? super T, ? extends U> f) throws InterruptedException {
        return run(values, threads,
                x -> x.map(f).collect(Collectors.toList()),
                x -> x.flatMap(List::stream).collect(Collectors.toList()));
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return run(values, threads, x -> x.max(comparator).orElse(null), x -> x.max(comparator).orElse(null));
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threads, values, Collections.reverseOrder(comparator));
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return run(values, threads, x -> x.allMatch(predicate), x -> x.allMatch(Boolean::booleanValue));
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return run(values, threads, x -> x.anyMatch(predicate), x -> x.anyMatch(Boolean::booleanValue));
    }
}
