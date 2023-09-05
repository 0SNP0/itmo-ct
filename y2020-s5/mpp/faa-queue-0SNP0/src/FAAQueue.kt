package mpp.faaqueue

import kotlinx.atomicfu.*

class FAAQueue<E> {
    private val head: AtomicRef<Segment> // Head pointer, similarly to the Michael-Scott queue (but the first node is _not_ sentinel)
    private val tail: AtomicRef<Segment> // Tail pointer, similarly to the Michael-Scott queue

    init {
        val firstNode = Segment()
        head = atomic(firstNode)
        tail = atomic(firstNode)
    }

    /**
     * Adds the specified element [x] to the queue.
     */
    fun enqueue(element: E) {
        while (true) {
            val cur = tail.value
            val i = cur.enqGetAndInc
            if (i < SEGMENT_SIZE) if (cur.add(element, i)) return else continue
            val new = Segment().apply { add(element) }
            if (cur.setNext(new)) {
                tail.compareAndSet(cur, new)
                return
            }
            cur.next?.let { tail.compareAndSet(cur, it) }
        }
    }

    /**
     * Retrieves the first element from the queue and returns it;
     * returns `null` if the queue is empty.
     */
    fun dequeue(): E? {
        while (true) {
            val cur = head.value
            val i = cur.deqGetAndInc
            if (i < SEGMENT_SIZE) cur.elements[i].getAndSet(removed)?.let { return it as? E }
            else cur.next?.let { head.compareAndSet(cur, it) } ?: return null
        }
    }

    /**
     * Returns `true` if this queue is empty, or `false` otherwise.
     */
    val isEmpty: Boolean
        get() {
            while (true) {
                val cur = head.value
                if (cur.deq < cur.enq && cur.deq < SEGMENT_SIZE) return false
                cur.next?.let { head.compareAndSet(cur, it) } ?: return true
            }
        }
}

private class Segment {
    val nextRef = atomic<Segment?>(null)
    val next get() = nextRef.value
    val elements = atomicArrayOfNulls<Any>(SEGMENT_SIZE)
    val enqIdx = atomic(0)
    val enq get() = enqIdx.value
    val enqGetAndInc get() = enqIdx.getAndIncrement()
    val deqIdx = atomic(0)
    val deq get() = deqIdx.value
    val deqGetAndInc get() = deqIdx.getAndIncrement()

    fun setNext(x: Segment) = nextRef.compareAndSet(null, x)
    fun add(value: Any?, i: Int = enqGetAndInc) = elements[i].compareAndSet(null, value)
    fun rem(i: Int = deqGetAndInc) = elements[i].getAndSet(removed)
}

const val SEGMENT_SIZE = 2 // DO NOT CHANGE, IMPORTANT FOR TESTS
private val removed = Any()
