package mpp.dynamicarray

import kotlinx.atomicfu.*

interface DynamicArray<E> {
    /**
     * Returns the element located in the cell [index],
     * or throws [IllegalArgumentException] if [index]
     * exceeds the [size] of this array.
     */
    fun get(index: Int): E

    /**
     * Puts the specified [element] into the cell [index],
     * or throws [IllegalArgumentException] if [index]
     * exceeds the [size] of this array.
     */
    fun put(index: Int, element: E)

    /**
     * Adds the specified [element] to this array
     * increasing its [size].
     */
    fun pushBack(element: E)

    /**
     * Returns the current size of this array,
     * it increases with [pushBack] invocations.
     */
    val size: Int
}

class DynamicArrayImpl<E> : DynamicArray<E> {
    private val core = atomic(Core<E>(INITIAL_CAPACITY))

    override fun get(index: Int): E = core.value.get(index)

    override fun put(index: Int, element: E) = core.value.put(index, element)

    override fun pushBack(element: E) {
        while (true) core.value.let {
            if (it.pushBack(element)) return
            core.compareAndSet(it, it.increase())
        }
    }

    override val size: Int get() = core.value.size
}

private class Core<E>(capacity: Int, size: Int = 0) {
    private val array = atomicArrayOfNulls<Cell<E>>(capacity)
    private val sizeRef = atomic(size)
    val size: Int get() = sizeRef.value
    private val nextRef = atomic(this)
    private val next get() = nextRef.value

    fun arrCAS(i: Int, expect: Cell<E>?, update: Cell<E>?) = array[i].compareAndSet(expect, update)

    fun get(index: Int): E =
        if (index < 0 || index >= size) throw IllegalArgumentException("index out of bound")
        else when (val cur = array[index].value) {
            is Main<E> -> cur.value
            is Remote<E> -> cur.value
            else -> next.get(index)
        }

    fun put(index: Int, element: E): Unit =
        if (index < 0 || index >= size) throw IllegalArgumentException("index out of bound")
        else while (true) when (val cur = array[index].value) {
            is Main<E> -> if (arrCAS(index, cur, Main(element))) break
            is Remote<E> -> {
                next.arrCAS(index, null, Main(cur.value))
                arrCAS(index, cur, Removed())
            }
            else -> {
                next.put(index, element)
                break
            }
        }

    fun pushBack(element: E): Boolean {
        while (true) size.let {
            if (it == array.size) return false
            if (arrCAS(it, null, Main(element))) {
                updSize(it)
                return true
            }
            updSize(it)
        }
    }

    fun increase(): Core<E> {
        if (next == this) nextRef.compareAndSet(this, Core(array.size * 2, size))
        for (index in 0 until size) while (true) when (val cur = array[index].value) {
            is Main<E> -> arrCAS(index, cur, Remote(cur.value))
            is Remote<E> -> {
                next.arrCAS(index, null, Main(cur.value))
                arrCAS(index, cur, Removed())
            }
            else -> break
        }
        return next
    }

    fun updSize(expected: Int) = sizeRef.compareAndSet(expected, expected + 1)
}

private interface Cell<E>
private class Main<E>(val value: E) : Cell<E>
private class Remote<E>(val value: E) : Cell<E>
private class Removed<E> : Cell<E>

private const val INITIAL_CAPACITY = 1 // DO NOT CHANGE ME