package queue;

public interface Queue {

//    MODEL: elements = [a[1], a[2], ..., a[size]] && size - размер очереди
//    INV: size >= 0 && forall i = 1..size : a[i] != null
//    Immutable: size == size' && forall i = 1..n: a[i] == a'[i]

//    PRE: elem != null
    void enqueue(Object elem);
//    POST: size = size' + 1 && a[size] == elem && forall i = 1..size': a[i] == a'[i]

//    PRE: size > 0
    Object element();
//    POST: R = elements[start] && Immutable

//    PRE: size > 0
    Object dequeue();
//    POST: R = a'[1] && size = size' - 1 && forall i = 1..size: a[i] = a'[i + 1]

//    PRE: true
    int size();
//    POST: R = size && Immutable

//    PRE: true
    boolean isEmpty();
//    POST: R = (size == 0) && Immutable

//    PRE: true
    void clear();
//    POST: size = 0 && elements is empty

//    PRE: elem != null
    boolean contains(Object elem);
//    POST: (elem contained in elements ? true : false) && Immutable

//    PRE: elem != null
    boolean removeFirstOccurrence(Object elem);
//    POST: contains(elem) ? (size = size' - 1 && elements' = [...a[i-1], R=a[i], a[i+1]...] && (forall k < i: a'[k] != R) && elements = [...a[i-1], a[i+1]...] && R = true) : (R = false && Immutable)
}
