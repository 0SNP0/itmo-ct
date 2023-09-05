package mpp.msqueue

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

class MSQueue<E> {
    private val head: AtomicRef<Node<E>>
    private val tail: AtomicRef<Node<E>>

    init {
        val dummy = Node<E>(null)
        head = atomic(dummy)
        tail = atomic(dummy)
    }

    /**
     * Adds the specified element [x] to the queue.
     */
    fun enqueue(x: E) {
        val new = Node(x)
        while (true) {
            val tmp = tail.value
            if (tmp.next.compareAndSet(null, new)) {
                tail.compareAndSet(tmp, new)
                break
            } else tail.compareAndSet(tmp, tmp.next.value!!)
        }
    }

    /**
     * Retrieves the first element from the queue
     * and returns it; returns `null` if the queue
     * is empty.
     */
    fun dequeue(): E? {
        while (true) {
            val h = head.value
            val t = tail.value
            val next = h.next.value
            if (h === t) {
                if (next == null) return null
                tail.compareAndSet(t, next)
            }
            else if (head.compareAndSet(h, next!!)) return next.x
        }
    }

    fun isEmpty(): Boolean = head.value === tail.value && head.value.next.value == null
}

private class Node<E>(val x: E?) {
    val next = atomic<Node<E>?>(null)
}