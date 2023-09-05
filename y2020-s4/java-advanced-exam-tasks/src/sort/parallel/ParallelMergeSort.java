package sort.parallel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelMergeSort<T> {
    private final T[] arr;
    private final T[] tmp;
    private final Comparator<T> comparator;
    private final ExecutorService threadPool;

    public ParallelMergeSort(T[] arr, Comparator<T> comparator, int threads) {
        this.threadPool = Executors.newWorkStealingPool(threads);
        this.arr = arr;
        this.tmp = Arrays.copyOf(arr, arr.length);
        this.comparator = comparator;
    }

    public static <E extends Comparable<E>> ParallelMergeSort<E> of(E[] arr, int threads) {
        return new ParallelMergeSort<E>(arr, E::compareTo, threads);
    }

    public void compute(int l, int r) throws InterruptedException {
        if (l >= r)
            return;
        int m = (l + r) / 2;
        threadPool.invokeAll(List.of(task(l, m), task(m + 1, r)));
        merge(l, m, r);
    }

    private Callable<Void> task(int l, int r) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                compute(l, r);
                return null;
            }
        };
    }

    public void compute() {
        try {
            compute(0, arr.length - 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void merge(int l, int m, int r) {
        for (int i = l; i <= r; i++) {
            tmp[i] = arr[i];
        }
        int ltmp = l;
        int rtmp = m + 1;
        int cur = l;
        while (ltmp <= m && rtmp <= r) {
            if (comparator.compare(tmp[ltmp], tmp[rtmp]) <= 0) {
                arr[cur] = tmp[ltmp++];
            } else {
                arr[cur] = tmp[rtmp++];
            }
            cur++;
        }
        while (ltmp <= m) {
            arr[cur++] = tmp[ltmp++];
        }
    }

    public static void main(String... args) {
        Integer[] arr = List.of(8, 6, 7, 5, 4, 3, 2, 1).toArray(new Integer[0]);
        ParallelMergeSort.of(arr, 2).compute();
        Arrays.stream(arr).forEach(System.out::println);
    }
}
