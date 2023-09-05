package dijkstra

import kotlinx.atomicfu.atomic
import java.util.*
import java.util.concurrent.Phaser
import kotlin.Comparator
import kotlin.concurrent.thread

private val NODE_DISTANCE_COMPARATOR = Comparator<Node> { o1, o2 -> Integer.compare(o1!!.distance, o2!!.distance) }

// Returns `Integer.MAX_VALUE` if a path has not been found.
fun shortestPathParallel(start: Node) {
    val workers = Runtime.getRuntime().availableProcessors()
    // The distance to the start node is `0`
    start.distance = 0
    // Create a priority (by distance) queue and add the start node into it
    val q = MQPQ(workers, NODE_DISTANCE_COMPARATOR)
    q.add(start)
    // Run worker threads and wait until the total work is done
    val onFinish = Phaser(workers + 1) // `arrive()` should be invoked at the end by each worker
    repeat(workers) {
        thread {
            while (true) {
                val cur: Node = synchronized(q) { q.poll() } ?: if (q.isEmpty()) break else continue
                for (e in cur.outgoingEdges) {
                    while (true) {
                        if (e.to.distance > cur.distance + e.weight) {
                            if (e.to.casDistance(e.to.distance, cur.distance + e.weight)) q.add(e.to)
                        } else break
                    }
                }
            }
            onFinish.arrive()
        }
    }
    onFinish.arriveAndAwaitAdvance()
}

private class MQPQ(workers: Int, private val comp: Comparator<Node>) {
    private val queues = List(workers * 4) { PriorityQueue(comp) }
    private val size = atomic(0)
    private val random = Random()
    private val rand get() = random.nextInt(queues.size)

    fun add(node: Node) {
        size.incrementAndGet()
        val q = queues[rand]
        synchronized(q) { q.add(node) }
    }

    fun poll(): Node? {
        val (a, b) = rand.let { a ->
            queues[a] to rand.let { if (it == a) queues[(it + 1) % queues.size] else queues[it] }
        }
        synchronized(a) {
            synchronized(b) {
                val res =
                    if (a.isEmpty()) if (b.isEmpty()) null else b.poll()
                    else if (b.isEmpty()) a.poll() else
                        if (comp.compare(a.peek(), b.peek()) < 0) a.poll() else b.poll()
                if (res != null) size.decrementAndGet()
                return res
            }
        }
    }

    fun isEmpty() = size.compareAndSet(0, 0)
}
