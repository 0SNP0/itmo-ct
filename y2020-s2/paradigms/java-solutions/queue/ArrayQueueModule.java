package queue;

public class ArrayQueueModule {

    private static final int startsize = 10;
    private static int start = 0;
    private static int size = 0;
    private static Object[] elements = new Object[startsize];
//    len = elements.length
//    tail = (start + size) % len
//    INV: queue in elements[[start..tail) or [start..len) and [0..tail)]
//    INV: each elem in queue : elem != null
//    INV: 0 <= start, tail < len

//    PRE: true
    public static void checkCapacity() {
        final int len = elements.length;
        if (size >= len - 1) {
            Object[] elems = new Object[len * 2];
            for (int i = 0; i < len - 1; i++) {
                elems[i] = elements[start];
                start = (start + 1) % len;
            }
            elements = elems;
            start = 0;
        }
    }
//    POST: len == len' || len == len' * 2

//    PRE: elem != null
    public static void enqueue(Object elem) {
        assert elem != null;
        checkCapacity();
        elements[(start + size++) % elements.length] = elem;
    }
//    POST: size = size' + 1 && each elem in elements' : elem in elements &&
//    (elements[tail - 1] == elem && 0 < tail < len || elements[len - 1] == elem && tail == 0)

//    PRE: elem != null
    public static void push(Object elem) {
        assert elem != null;
        checkCapacity();
        start = (start + elements.length - 1) % elements.length;
        elements[start] = elem;
        size++;
    }
//    POST: size = size' + 1 && each elem in elements' : elem in elements && elements[start] == elem

//    PRE: size > 0
    public static Object element() {
        assert size > 0;
        return elements[start];
    }
//    POST: R == elements[start] && size = size' && elements = elements'

//    PRE: size > 0
    public static Object peek() {
        assert size > 0;
        return elements[(start + size - 1) % elements.length];
    }
//    POST: (R == elements[tail - 1] && 0 < tail < len || R == elements[len - 1] && tail == 0) && size = size' && elements = elements'

//    PRE: size > 0
    public static Object dequeue() {
        assert size > 0;
        Object elem = elements[start];
        size--;
        start = (start + 1) % elements.length;
        return elem;
    }
//    POST: R == elements[start'] && size = size' - 1 && elements = elements' without R

//    PRE: size > 0
    public static Object remove() {
        assert size > 0;
        return elements[(start + --size) % elements.length];
    }
//    POST: (R == elements[tail - 1] && 0 < tail < len || R == elements[len - 1] && tail == 0) && size = size' - 1 && elements = elements' without R

//    PRE: true
    public static int size() {
        return size;
    }
//    POST: R == size

//    PRE: true
    public static boolean isEmpty() {
        return size == 0;
    }
//    POST: R == (size == 0)

//    PRE: true
    public static void clear() {
        elements = new Object[startsize];
        size = 0;
        start = 0;
    }
//    POST: start == 0 && size == 0
}
