import java.util.concurrent.atomic.*

class Solution(val env: Environment) : Lock<Solution.Node> {
    private val head = AtomicReference<Node>()

    override fun lock(): Node {
        val my = Node() // сделали узел
        my.locked = true
        head.getAndSet(my)?.let {
            it.next = my
            while (my.locked) env.park()
        }
        return my // вернули узел
    }

    override fun unlock(node: Node) {
        if (node.next == null && head.compareAndSet(node, null)) return
        while (node.next?.let {
                it.locked = false
                env.unpark(it.thread)
            } == null) continue
    }

    class Node {
        val thread: Thread = Thread.currentThread() // запоминаем поток, которые создал узел
        private val lockedRef = AtomicReference(false)
        private val nextRef = AtomicReference<Node>()
        var locked: Boolean
            set(value) {
                lockedRef.value = value
            }
            get() = lockedRef.value
        var next: Node?
            set(value) = nextRef.set(value)
            get() = nextRef.get()
    }
}