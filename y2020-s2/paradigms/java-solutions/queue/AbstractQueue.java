package queue;

public abstract class AbstractQueue implements Queue {

    protected int size = 0;

    protected abstract void addBack(Object elem);
    protected abstract Object getFront();
    protected abstract void removeFront();

    @Override
    public void enqueue(Object elem) {
        assert elem != null;
        addBack(elem);
        size++;
    }

    @Override
    public Object element() {
        assert size > 0;
        return getFront();
    }

    @Override
    public Object dequeue() {
        assert size > 0;
        Object res = getFront();
        removeFront();
        size--;
        return res;
    }

    abstract boolean findAndMaybeRemove(final Object elem, final boolean remove);

    @Override
    public boolean contains(final Object elem) {
        return findAndMaybeRemove(elem, false);
    }

    @Override
    public boolean removeFirstOccurrence(final Object elem) {
        if (findAndMaybeRemove(elem, true)) {
            size--;
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }
}
