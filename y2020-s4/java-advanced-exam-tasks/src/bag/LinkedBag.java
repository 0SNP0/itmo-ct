package bag;

import java.util.*;

// :NOTE: # Не использован стандартный базовый класс
// :NOTE: # Ассимптотически не эффективно
// :NOTE: * Самописный LinkedList без преимуществ
public class LinkedBag<E> implements Collection<E> {

    private int size = 0; // :NOTE: Бесполезный инициализатор
    private Node<E> first;
    private Node<E> last;

    public LinkedBag() {
    }

    public LinkedBag(Collection<? extends E> collection) {
        addAll(collection);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return search(o) != null;
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedBagIterator(first);
    }

    @Override
    public Object[] toArray() {
        Object[] res = new Object[size];
        int i = 0;
        for (Node<E> node = first; node != null; node = node.next)
            res[i++] = node.elem;
        return res;
    }

    // :NOTE: # Не соответствует спецификации
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) new Object[size];
        }
        Object[] res = a;
        int i = 0;
        for (Node<E> node = first; node != null; node = node.next)
            res[i++] = node.elem;
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(E e) {
        Node<E> node = new Node<>(e, last, null);
        if (last == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
        size++;
        return true;
    }

    // :NOTE: Линейное время
    @Override
    public boolean remove(Object o) {
        Node<E> node = search(o);
        if (node == null) {
            return false;
        }
        unlink(node);
        return true;
    }

    // :NOTE: Линейное время
    private Node<E> search(Object o) {
        for (Node<E> node = first; node != null; node = node.next) {
            if (o == null && node.elem == null || o != null && o.equals(node.elem)) {
                return node;
            }
        }
        return null;
    }

    private void unlink(Node<E> node) {
        if (node.prev == null) {
            first = node.next;
        } else {
            node.prev.next = node.next;
        }
        if (node.next == null) {
            last = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        size--;
        node.prev = node.next = null;
        node.elem = null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return (new HashSet<>(this)).containsAll(new HashSet<>(c));
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }
        for (E e : c) {
            add(e);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        HashSet<?> set = new HashSet<>(c);
        return removeIf(set::contains);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        HashSet<?> set = new HashSet<>(c);
        return removeIf(x -> !set.contains(x));
    }

    @Override
    public void clear() {
        while (first != null) {
            Node<E> next = first.next;
            first.elem = null;
            first.next = first.prev = null;
            first = next;
        }
        last = null;
        size = 0;
    }

    private static class Node<E> {
        E elem;
        Node<E> next;
        Node<E> prev;

        Node(E element, Node<E> prev, Node<E> next) {
            this.elem = element;
            this.prev = prev;
            this.next = next;
        }
    }

    private class LinkedBagIterator implements Iterator<E> {

        private Node<E> node;
        private Node<E> prev;

        private LinkedBagIterator(Node<E> node) {
            this.node = node;
            this.prev = null;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public E next() {
            if (hasNext()) {
                prev = node;
                E res = node.elem;
                node = node.next;
                return res;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            if (prev == null) {
                throw new IllegalStateException("Already removed");
            }
            unlink(prev);
            prev = null;
        }
    }
}

