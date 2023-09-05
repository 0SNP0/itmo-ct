package queue;

public class ArrayQueueADT {

    private final int startsize = 10;
    private int start = 0;
    private int size = 0;
    private Object[] elements = new Object[startsize];
//    len = elements.length
//    tail = (start + size) % len
//    INV: queue in elements[[start..tail) or [start..len) and [0..tail)]
//    INV: each elem in queue : elem != null
//    INV: 0 <= start, tail < len
//    $ = queue.$ : $ - field od ArrayQueueADT

//    PRE: queue != null && true
    public static void checkCapacity(ArrayQueueADT queue) {
        final int len = queue.elements.length;
        if (queue.size == len - 1) {
            Object[] elems = new Object[len * 2];
            for (int i = 0; i < len - 1; i++) {
                elems[i] = queue.elements[queue.start];
                queue.start = (queue.start + 1) % len;
            }
            queue.elements = elems;
            queue.start = 0;
        }
    }
//    POST: len == len' || len == len' * 2

//    PRE: queue != null && elem != null
    public static void enqueue(ArrayQueueADT queue, Object elem) {
        assert elem != null;
        checkCapacity(queue);
        queue.elements[(queue.start + queue.size++) % queue.elements.length] = elem;
    }
//    POST: size = size' + 1 && each elem in elements' : elem in elements &&
//    (elements[tail - 1] == elem && 0 < tail < len || elements[len - 1] == elem && tail == 0)

//    PRE: queue != null && elem != null
    public static void push(ArrayQueueADT queue, Object elem) {
        assert elem != null;
        checkCapacity(queue);
        queue.start = (queue.start + queue.elements.length - 1) % queue.elements.length;
        queue.elements[queue.start] = elem;
        queue.size++;
    }
//    POST: size = size' + 1 && each elem in elements' : elem in elements && elements[start] == elem

//    PRE: queue != null && size > 0
    public static Object element(ArrayQueueADT queue) {
        assert queue.size > 0;
        return queue.elements[queue.start];
    }
//    POST: R == elements[start] && size = size' && elements = elements'

//    PRE: queue != null && size > 0
    public static Object peek(ArrayQueueADT queue) {
        assert queue.size > 0;
        return queue.elements[(queue.start + queue.size - 1) % queue.elements.length];
    }
//    POST: (R == elements[tail - 1] && 0 < tail < len || R == elements[len - 1] && tail == 0) && size = size' && elements = elements'

//    PRE: queue != null && size > 0
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue.size > 0;
        Object elem = queue.elements[queue.start];
        queue.size--;
        queue.start = (queue.start + 1) % queue.elements.length;
        return elem;
    }
//    POST: R == elements[start'] && size = size' - 1 && elements = elements' without R

//    PRE: queue != null && size > 0
    public static Object remove(ArrayQueueADT queue) {
        assert queue.size > 0;
        return queue.elements[(queue.start + --queue.size) % queue.elements.length];
    }
//    POST: (R == elements[tail - 1] && 0 < tail < len || R == elements[len - 1] && tail == 0) && size = size' - 1 && elements = elements' without R

//    PRE: queue != null && true
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }
//    POST: R == size

//    PRE: queue != null && true
    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }
//    POST: R == (size == 0)

//    PRE: queue != null && true
    public static void clear(ArrayQueueADT queue) {
        queue.elements = new Object[queue.startsize];
        queue.size = 0;
        queue.start = 0;
    }
//    POST: start == 0 && size == 0
}
