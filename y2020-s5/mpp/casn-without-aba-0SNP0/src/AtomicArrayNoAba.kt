import kotlinx.atomicfu.*

class AtomicArrayNoAba<E>(size: Int, initialValue: E) {
    private val a = Array(size) { Ref(initialValue) }

    fun get(index: Int) =
        a[index].value as E

    fun cas(index: Int, expected: E, update: E) =
        a[index].compareAndSet(expected, update)

    fun cas2(index1: Int, expected1: E, update1: E,
             index2: Int, expected2: E, update2: E): Boolean {
        if (index1 == index2) {
            if (expected1 == expected2 && update1 is Int) return cas(index1, expected1, ((update1 as Int) + 1) as E)
            //aaa aa aa aaa a aaa aa/a/aa/a/aa/aa/a/aaa/aa/aaa/a/a/aa/a
            if (update1 != update2) return false
        }
        return Descriptor::result.get(
            if (index1 < index2) CAS2Descriptor(a[index1], expected1, update1, a[index2], expected2, update2).also {
                if (!a[index1].compareAndSet(expected1, it)) return false
            } else CAS2Descriptor(a[index2], expected2, update2, a[index1], expected1, update1).also {
                if (!a[index2].compareAndSet(expected2, it)) return false
            }
        )
    }
}

private class Ref(value: Any?) {
    val ref = atomic(value)

    var value: Any?
        get() {
            while (true) ref.value.let { if (it is Descriptor) it.perform() else return it }
        }
        set(new) {
            while (true) ref.value.let {
                if (it is Descriptor) it.perform() else if (ref.compareAndSet(it, new)) return
            }
        }

    fun compareAndSet(expect: Any?, update: Any?): Boolean {
        while (true)
            if (ref.compareAndSet(expect, update)) return true
            else when (val it = ref.value) {
                is Descriptor -> it.perform()
                else -> if (expect != it) return false
            }
    }
}

private abstract class Descriptor(val consensus: Ref = Ref(null)) {
    val result: Boolean get() = perform().let { consensus.value as Boolean }
    abstract fun perform()
}

private class DCSSDescriptor(
    val ref1: Ref, val expected1: Any?, val update1: Any?, val ref2: Ref, val expected2: Any?,
) : Descriptor() {
    override fun perform(): Unit = consensus.ref.let {
        it.compareAndSet(null, expected2 == ref2.value)
        if (it.value as Boolean) ref1.ref.compareAndSet(this, update1)
        else ref1.ref.compareAndSet(this, expected1)
    }
}

private class CAS2Descriptor(
    val ref1: Ref, val expected1: Any?, val update1: Any?,
    val ref2: Ref, val expected2: Any?, val update2: Any?,
) : Descriptor() {
    override fun perform(): Unit = consensus.ref.let { ref ->
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