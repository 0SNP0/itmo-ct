package mpp.counter

import kotlinx.atomicfu.AtomicIntArray
import java.util.Random

class ShardedCounter {
    private val counters = AtomicIntArray(ARRAY_SIZE)

    /**
     * Atomically increments by one the current value of the counter.
     */
    fun inc() {
        counters[Random().nextInt(ARRAY_SIZE)].incrementAndGet()
    }

    /**
     * Returns the current counter value.
     */
    fun get(): Int {
        var res = 0
        repeat(ARRAY_SIZE) { res += counters[it].value }
        return res
    }
}

private const val ARRAY_SIZE = 2 // DO NOT CHANGE ME