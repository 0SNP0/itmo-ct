package info.kgeorgiy.ja.selivanov.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {

    private final ReversibleList<E> data;
    private final Comparator<? super E> comparator;

    public ArraySet(final Collection<? extends E> collection, final Comparator<? super E> comparator) {
        this.comparator = comparator;
        SortedSet<E> set = new TreeSet<>(comparator);
        set.addAll(collection);
        data = new ReversibleList<>(set);
    }

    public ArraySet(final Collection<? extends E> collection) {
        this(collection, null);
    }

    private ArraySet(final ReversibleList<E> list, final Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.data = list;
    }

    public ArraySet(final Comparator<? super E> comparator) {
        this(new ReversibleList<>(), comparator);
    }

    public ArraySet() {
        this(new ReversibleList<>(), null);
    }

    private E getByIndex(int index) {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return data.get(index);
    }

    private E getByIndexOrNull(int index) {
        return 0 <= index && index < size() ? data.get(index) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        Objects.requireNonNull(o);
        return Collections.binarySearch(data, (E) o, comparator) >= 0;
    }

    private int indexOf(E element, Border border) {
        final int result = Collections.binarySearch(data, element, comparator);
        if (result < 0) {
            return (border.lower ? -1 : 0) - 1 - result;
        } else if (border.inclusive) {
            return result;
        } else {
            return result + (border.lower ? -1 : 1);
        }
    }

    @Override
    public E lower(E e) {
        return getByIndexOrNull(indexOf(e, Border.LOWER));
    }

    @Override
    public E floor(E e) {
        return getByIndexOrNull(indexOf(e, Border.FLOOR));
    }

    @Override
    public E ceiling(E e) {
        return getByIndexOrNull(indexOf(e, Border.INCLUSIVE));
    }

    @Override
    public E higher(E e) {
        return getByIndexOrNull(indexOf(e, Border.HIGH));
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(data).iterator();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(data.reversed(), Collections.reverseOrder(comparator));
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    @SuppressWarnings("unchecked")
    private int compare(final E first, final E second) {
        if (comparator == null) {
            return ((Comparable<E>) first).compareTo(second);
        }
        return comparator.compare(first, second);
    }

    private NavigableSet<E> subSetUnchecked(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        final int from = indexOf(fromElement, Border.of(false, fromInclusive));
        final int to = indexOf(toElement, Border.of(true, toInclusive));
        return new ArraySet<>(from > to ? new ReversibleList<>() : new ReversibleList<>(data.subList(from, to + 1)), comparator);
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        if (compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException();
        }
        return subSetUnchecked(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        if (isEmpty()) {
            return this;
        }
        return subSetUnchecked(first(), true, toElement, inclusive);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        if (isEmpty()) {
            return this;
        }
        return subSetUnchecked(fromElement, inclusive, last(), true);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public E first() {
        return getByIndex(0);
    }

    @Override
    public E last() {
        return getByIndex(size() - 1);
    }

    @Override
    public int size() {
        return data.size();
    }

    private static class ReversibleList<T> extends AbstractList<T> implements RandomAccess {
        private final List<T> data;
        private final boolean reversed;

        private ReversibleList(final Collection<T> list, final boolean reversed) {
            this.data = List.copyOf(list);
            this.reversed = reversed;
        }

        private ReversibleList(final Collection<T> list) {
            this(list, false);
        }

        private ReversibleList() {
            this(Collections.emptyList());
        }

        private ReversibleList<T> reversed() {
            return new ReversibleList<>(data, !reversed);
        }

        @Override
        public T get(int index) {
            return reversed ? data.get(size() - index - 1) : data.get(index);
        }

        @Override
        public int size() {
            return data.size();
        }
    }

    private enum Border {
        HIGH(false, false),
        LOWER(true, false),
        INCLUSIVE(false, true),
        FLOOR(true, true);

        final boolean lower, inclusive;

        Border(final boolean lower, final boolean inclusive) {
            this.lower = lower;
            this.inclusive = inclusive;
        }

        public static Border of(boolean lower, boolean inclusive) {
            if (lower) {
                if (inclusive) return FLOOR;
                else return LOWER;
            } else {
                if (inclusive) return INCLUSIVE;
                else return HIGH;
            }
        }
    }
}
