package mpp.linkedlistset

import kotlinx.atomicfu.*

class LinkedListSet<E : Comparable<E>> {
    private val first = Node(element = object : T() {
        override fun compareTo(other: LinkedListSet<E>.T): Int = if (other === this) 0 else -1
    }, next = null)
    private val last = Node(element = object : T() {
        override fun compareTo(other: LinkedListSet<E>.T): Int = if (other === this) 0 else 1
    }, next = null)

    init {
        first.setNext(last)
    }

    private val head = atomic(first)

    /**
     * Adds the specified element to this set
     * if it is not already present.
     *
     * Returns `true` if this set did not
     * already contain the specified element.
     */
    fun add(element: E): Boolean {
        val it = TE(element)
        while (true) {
            val (cur, next) = find(it)
            if (it == next.element) return false
            if (cur.casNext(next, Node(it, next))) return true
        }
    }

    /**
     * Removes the specified element from this set
     * if it is present.
     *
     * Returns `true` if this set contained
     * the specified element.
     */
    fun remove(element: E): Boolean {
        val it = TE(element)
        while (true) {
            val (cur, next) = find(it, true)
            if (it != cur.element) return false
            if (cur.casNext(next, PrevRemoved(next))) return true
        }
    }

    /**
     * Returns `true` if this set contains
     * the specified element.
     */
    fun contains(element: E): Boolean = TE(element).let { find(it).second.element == it }

    private fun find(element: T, strong: Boolean = false): Pair<Node, Node> {
        while (true) {
            var cur = head.value
            var next = cur.next!!
            while ((if (strong) element >= next.element else element > next.element) || next is PrevRemoved) {
                if (cur is PrevRemoved) break
                if (next is PrevRemoved) next.next.next.let {
                    next = if (cur.casNext(next, it)) it!! else cur.next!!
                } else {
                    cur = next
                    next = cur.next!!
                }
            }
            if (cur !is PrevRemoved) return cur to next
        }
    }

    private open inner class Node(val element: T, next: Node?) {

        private val _next = atomic(next)
        open val next get() = _next.value
        fun setNext(value: Node?) { _next.value = value }

        fun casNext(expected: Node?, update: Node?) = _next.compareAndSet(expected, update)
    }

    private inner class PrevRemoved(next: Node) : Node(first.element, next) {
        override val next get() = super.next!!
    }

    private abstract inner class T : Comparable<T>
    private inner class TE(val x: E) : T() {
        override fun compareTo(other: T): Int =
            if (other is TE) x.compareTo(other.x)
            else -other.compareTo(this)

        override fun equals(other: Any?): Boolean = if (other is LinkedListSet<*>.TE) x == other.x else false
        override fun hashCode(): Int {
            return x.hashCode()
        }
    }
}