import kotlinx.atomicfu.*

class AtomicArray<E>(size: Int, initialValue: E) {
    private val a = Array(size) { Ref(initialValue) }

    @Suppress("UNCHECKED_CAST")
    fun get(index: Int) = a[index].value as? E

    fun set(index: Int, value: E) {
        a[index].value = value
    }

    fun cas(index: Int, expected: E, update: E) =
        a[index].compareAndSet(expected, update)

    fun cas2(
        index1: Int, expected1: E, update1: E,
        index2: Int, expected2: E, update2: E,
    ): Boolean =
        if (index1 == index2)
            if (expected1 == expected2) cas(index1, expected1, update2) else false
        else
            if (index1 < index2) CAS2Descriptor(a[index1], expected1, update1, a[index2], expected2, update2)
                .let { if (a[index1].compareAndSet(expected1, it)) it.result else false }
            else CAS2Descriptor(a[index2], expected2, update2, a[index1], expected1, update1)
                .let { if (a[index2].compareAndSet(expected2, it)) it.result else false }
}

private class Ref(value: Any?) {
    val ref = atomic(value)
    var value: Any?
        get() {
            while (true) when (val it = ref.value) {
                is Descriptor -> it.complete()
                else -> return it
            }
        }
        set(new) {
            while (true) when (val it = ref.value) {
                is Descriptor -> it.complete()
                ref.compareAndSet(it, new) -> return
            }
        }

    fun compareAndSet(expect: Any?, update: Any?): Boolean {
        while (true)
            if (ref.compareAndSet(expect, update)) return true
            else when (val it = ref.value) {
                is Descriptor -> it.complete()
                else -> if (expect != it) return false
            }
    }
}

private abstract class Descriptor(val consensus: Ref = Ref(null)) {
    val result: Boolean get() = complete().let { consensus.value as Boolean }
    abstract fun complete()
}

private class DCSSDescriptor(
    val ref1: Ref, val expected1: Any?, val update1: Any?, val ref2: Ref, val expected2: Any?,
) : Descriptor() {
    override fun complete(): Unit = consensus.ref.let {
        it.compareAndSet(null, expected2 == ref2.value)
        if (it.value as Boolean) ref1.ref.compareAndSet(this, update1)
        else ref1.ref.compareAndSet(this, expected1)
    }
}

private class CAS2Descriptor(
    val ref1: Ref, val expected1: Any?, val update1: Any?,
    val ref2: Ref, val expected2: Any?, val update2: Any?,
) : Descriptor() {
    override fun complete(): Unit = consensus.ref.let { ref ->
        (this == ref2.ref.value || DCSSDescriptor(ref2, expected2, this, consensus, null).let {
            ref2.compareAndSet(expected2, it) && it.result
        }).let { ref.compareAndSet(null, it) }
        if (ref.value as Boolean) {
            ref1.ref.compareAndSet(this, update1)
            ref2.ref.compareAndSet(this, update2)
        } else {
            ref1.ref.compareAndSet(this, expected1)
            ref2.ref.compareAndSet(this, expected2)
        }
    }
}