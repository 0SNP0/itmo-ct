package lists;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

// :NOTE: # Реализация списков "с нуля"
public class LazyList<E> implements List<E> {

    private final int size;
    private final IntFunction<E> function;
    private final List<Status> status; // :NOTE: * BitSet
    private final List<E> result;

    public LazyList(final int size, final IntFunction<E> function) {
        this.size = size;
        this.function = function;
        this.status = IntStream.range(0, size).mapToObj(x -> new Status()).toList();
        this.result = new ArrayList<>(Collections.nCopies(size, null));
    }

    private LazyList(final int size, final IntFunction<E> function, final List<Status> status, final List<E> result) {
        this.size = size;
        this.function = function;
        this.status = status;
        this.result = result;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size != 0;
    }

    @Override
    public boolean contains(final Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public Object[] toArray() {
        return IntStream.range(0, size).mapToObj(this::get).toArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    // :NOTE: * Некорректная реализация
    public <T> T[] toArray(final T[] a) {
        if (a.length < size) {
            return (T[]) toArray();
        }
        IntStream.range(0, size).forEach(i -> a[i] = (T) get(i));
        if (a.length > size)
            a[size] = null;
        return a;
    }

    @Override
    public boolean add(final E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return (new HashSet<>(this)).containsAll(new HashSet<>(c));
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(final int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        synchronized (status.get(index)) {
            if (status.get(index).val) {
                return result.get(index);
            }
            final FutureTask<E> task = new FutureTask<>(() -> function.apply(index));
            // :NOTE: * Создание нового потока для вычисления значения
            final Thread thread = new Thread(task);
            thread.start();
            try {
                result.set(index, task.get());
            } catch (final InterruptedException | ExecutionException e) {
                // :NOTE: * Неверная обработка ошибок
                // :NOTE: * Повторное вычисление в случае ошибки
                throw new RuntimeException(e);
            }
            status.get(index).val = true;
        }
        return result.get(index);
    }

    @Override
    public E set(final int index, final E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(final int index, final E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(final int index) {
        throw new UnsupportedOperationException();
    }

    private boolean compare(final Object o, final int index) {
        return o == null && get(index) == null || o != null && o.equals(get(index));
    }

    @Override
    public int indexOf(final Object o) {
        for (int i = 0; i < size; i++) {
            if (compare(o, i)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(final Object o) {
        for (int i = size - 1; i >= 0; i--) {
            if (compare(o, i)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new LazyListIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return new LazyListIterator(index);
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        return new LazyList<>(toIndex - fromIndex, i -> this.get(i + fromIndex),
                status.subList(fromIndex, toIndex), result.subList(fromIndex, toIndex));
    }

    private class LazyListIterator implements ListIterator<E> {

        private int index;

        LazyListIterator(final int index) {
            if (index < 0 || index > size) {
                throw new IndexOutOfBoundsException();
            }
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public E next() {
            try {
                return get(index++);
            } catch (final IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public E previous() {
            try {
                return get(--index);
            } catch (final IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(final E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(final E e) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Status {
        boolean val = false;
    }
}
