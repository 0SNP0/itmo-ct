package queue;

public class ArrayQueue extends AbstractQueue {
    private final int startsize = 10;
    private int start = 0;
    private Object[] elements = new Object[startsize];
//    len = elements.length
//    tail = (start + size) % len
//    INV: queue in elements[[start..tail) or [start..len) and [0..tail)]
//    INV: each elem in queue : elem != null
//    INV: 0 <= start, tail < len

    private void copyArrayTo(Object[] copy) {
        System.arraycopy(elements, start, copy, 0, elements.length - start);
        if (start + size > elements.length) {
            System.arraycopy(elements, 0, copy, elements.length - start,(start + size) % elements.length);
        }
    }

//    PRE: true
    public void checkCapacity() {
        final int len = elements.length;
        if (size == len - 1) {
            Object[] queue = new Object[len * 2];
            copyArrayTo(queue);
            elements = queue;
            start = 0;
        }
    }
//    POST: len == len' || len == len' * 2


    @Override
    protected void addBack(Object elem) {
        checkCapacity();
        elements[(start + size) % elements.length] = elem;
    }

    @Override
//    PRE: size > 0
    protected Object getFront() {
        return elements[start];
    }
//    POST: R == elements[start] && size = size' && elements = elements'

    @Override
//    PRE: size > 0
    protected void removeFront() {
//        elem = elements[start];
        start = (start + 1) % elements.length;
    }
//    POST: start = (start' + 1) % elements.length;

//    PRE: true
    public boolean isEmpty() {
        return size == 0;
    }
//    POST: R == (size == 0)

//    PRE: true
    @Override
    public void clear() {
        elements = new Object[startsize];
        size = 0;
        start = 0;
    }
//    POST: start == 0 && size == 0

    @Override
    boolean findAndMaybeRemove(final Object elem, final boolean remove) {
        for (int i = 0; i < size; i++) {
            if (elements[(start + i) % elements.length].equals(elem)) {
                if (remove) {
                    Object[] new1 = new Object[elements.length];
                    copyArrayTo(new1);
                    elements = new Object[elements.length];
                    System.arraycopy(new1, 0, elements, 0, i);
                    System.arraycopy(new1, i + 1, elements, i, size - 1 - i);
                    start = 0;
                }
                return true;
            }
        }
        return false;
    }

//    PRE: elem != null
    public void push(Object elem) {
        assert elem != null;
        checkCapacity();
        start = (start + elements.length - 1) % elements.length;
        elements[start] = elem;
        size++;
    }
//    POST: size = size' + 1 && each elem in elements' : elem in elements && elements[start] == elem

//    PRE: size > 0
    public Object peek() {
        assert size > 0;
        return elements[(start + size - 1) % elements.length];
    }
//    POST: (R == elements[tail - 1] && 0 < tail < len || R == elements[len - 1] && tail == 0) && size = size' && elements = elements'

//    PRE: size > 0
    public Object remove() {
        assert size > 0;
        return elements[(start + --size) % elements.length];
    }
//    POST: (R == elements[tail - 1] && 0 < tail < len || R == elements[len - 1] && tail == 0) && size = size' - 1 && elements = elements' without R
}
