package queue;

public class LinkedQueue extends AbstractQueue {

    private Node front, back;

    @Override
    protected void addBack(Object elem) {
        if (isEmpty()) {
            front = back = new Node(elem);
        } else {
            back = back.setNext(new Node(elem));
        }
    }

    @Override
    protected Object getFront() {
        return front.item();
    }

    @Override
    protected void removeFront() {
        Node current = front;
        front = front.next();
        current.setNext(null);
    }

    @Override
    public void clear() {
        size = 0;
        front = back = null;
    }

    @Override
    boolean findAndMaybeRemove(final Object elem, final boolean remove) {
        Node prev = null, current = front;
        while (current != null) {
            if (current.item().equals(elem)) {
                if (remove) {
                    if (prev == null) {
                        front = current.next();
                    } else {
                        prev.setNext(current.next());
                    }
                    if (current.next() == null) {
                        back = prev;
                    }
                    current.setNext(null);
                }
                return true;
            }
            prev = current;
            current = current.next();
        }
        return false;
    }
}

class Node {
    private Object item;
    private Node next;
    public Node(Object item) {
        this.item = item;
    }
    public Object item() {
        return item;
    }
    public Node next() {
        return next;
    }
    public Node setNext(Node next) {
        return this.next = next;
    }
}