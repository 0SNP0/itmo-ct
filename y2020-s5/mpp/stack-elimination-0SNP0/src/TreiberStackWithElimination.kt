package mpp.stackWithElimination

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.atomicArrayOfNulls
import java.util.*

class TreiberStackWithElimination<E> {
    private val top = atomic<Node<E>?>(null)
    private val eliminationArray = atomicArrayOfNulls<Any?>(ELIMINATION_ARRAY_SIZE)
    private val rand get() = Random().nextInt(ELIMINATION_ARRAY_SIZE)

    /**
     * Adds the specified element [x] to the stack.
     */
    fun push(x: E) {
        elimination@ for (i in rand.let { (it until ELIMINATION_ARRAY_SIZE) + (0 until it) })
            for (j in 0..15) {
                if (!eliminationArray[i].compareAndSet(null, x)) continue
                repeat(1000) {}
                if (eliminationArray[i].compareAndSet(x, null)) break@elimination
                return
            }
        while (true) {
            val cur = top.value
            val new = Node(x, cur)
            if (top.compareAndSet(cur, new)) return
        }
    }

    /**
     * Retrieves the first element from the stack
     * and returns it; returns `null` if the stack
     * is empty.
     */
    fun pop(): E? {
        for (i in rand.let { (it until ELIMINATION_ARRAY_SIZE) + (0 until it) })
            repeat(4) {
                (eliminationArray[i].value as? E)?.let {
                    if (eliminationArray[i].compareAndSet(it, null)) return it
                }
            }
        while (true) {
            val cur = top.value
            val new = cur?.next
            if (top.compareAndSet(cur, new)) return cur?.x
        }
    }
}

private class Node<E>(val x: E, val next: Node<E>?)

private const val ELIMINATION_ARRAY_SIZE = 2 // DO NOT CHANGE IT
