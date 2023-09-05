import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.atomicArrayOfNulls
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class FCPriorityQueue<E : Comparable<E>> {
    private val q = PriorityQueue<E>()
    private val ops = atomicArrayOfNulls<Op<E>>(ARRAY_SIZE)
    private val locked = atomic(false)
    private val ind = ThreadLocalRandom.current().nextInt(ARRAY_SIZE)

    /**
     * Retrieves the element with the highest priority
     * and returns it as the result of this function;
     * returns `null` if the queue is empty.
     */
    fun poll(): E? = run(q::poll)

    /**
     * Returns the element with the highest priority
     * or `null` if the queue is empty.
     */
    fun peek(): E? = run(q::peek)

    /**
     * Adds the specified element to the queue.
     */
    fun add(element: E) {
        run {q.add(element).let { null }}
    }

    private fun run(x: () -> E?): E? = Op(x).let { op ->
        while (true) if (ops[ind].compareAndSet(null, op) && locked.compareAndSet(expect = false, update = true)) {
            repeat(ops.size) { ops[it].value?.apply { res.compareAndSet(null, fn()) } }
            locked.compareAndSet(expect = true, update = false)
            ops[ind].value = null
            break
        }
        op.res.value
    }
}

class Op<E>(val fn: () -> E?, val res: AtomicRef<E?> = atomic(null))

val ARRAY_SIZE = Thread.activeCount() * 2