import kotlinx.atomicfu.*
import kotlin.coroutines.*

/**
 * An element is transferred from sender to receiver only when [send] and [receive]
 * invocations meet in time (rendezvous), so [send] suspends until another coroutine
 * invokes [receive] and [receive] suspends until another coroutine invokes [send].
 */
class SynchronousQueue<E> {
    private val head: AtomicRef<Node> = atomic(Node())
    private val tail: AtomicRef<Node> = atomic(head.value)

    /**
     * Sends the specified [element] to this channel, suspending if there is no waiting
     * [receive] invocation on this channel.
     */
    suspend fun send(element: E) {
        while (true) {
            val cur = head.value to tail.value
            if (cur.first == cur.second || cur.second is Send)
                sc<Unit>(cur.second, { Send(element, it) })?.let { return }
            else (cur.first.next.value as? Receive)?.let {
                if (head.compareAndSet(cur.first, it)) {
                    it.cont.resume(element)
                    return
                }
            }
        }
    }

    /**
     * Retrieves and removes an element from this channel if there is a waiting [send] invocation on it,
     * suspends the caller if this channel is empty.
     */
    suspend fun receive(): E {
        while (true) {
            val cur = head.value to tail.value
            if (cur.first === cur.second || cur.second is Receive)
                sc<E>(cur.second, { Receive(it) })?.let { return it }
            else (cur.first.next.value as? Send)?.let {
                if (head.compareAndSet(cur.first, it)) {
                    it.cont.resume(Unit)
                    return it.element
                }
            }
        }
    }

    private suspend fun <T> sc(cur: Node, construct: (Continuation<T>) -> Node): T? = suspendCoroutine sc@{
        cur.next.value?.let { tail.compareAndSet(cur, it) } ?: construct(it).let { new ->
            if (cur.next.compareAndSet(null, new)) {
                tail.compareAndSet(cur, new)
                return@sc
            }
        }
        it.resume(null)
    }

    private open inner class Node(val next: AtomicRef<Node?> = atomic(null))
    private inner class Send(val element: E, val cont: Continuation<Unit>) : Node()
    private inner class Receive(val cont: Continuation<E>) : Node()
}