import java.util.*

var sz = 0
lateinit var intree: Array<Node?>
lateinit var tree: Array<yNode?>

fun main() {
    val scanner = Scanner(System.`in`)
    val n = scanner.nextInt()
    intree = arrayOfNulls(2 * n)
    var min = Int.MAX_VALUE
    var max = Int.MIN_VALUE
    for (i in 0 until n) {
        val x1 = scanner.nextInt()
        val y1 = scanner.nextInt()
        val x2 = scanner.nextInt()
        val y2 = scanner.nextInt()
        min = min.coerceAtMost(y1)
        max = max.coerceAtLeast(y2)
        intree[2 * i] = Node(x1, y2, y1, 1)
        intree[2 * i + 1] = Node(x2, y2, y1, -1)
    }
    Arrays.sort(intree)
    if (min > 0) {
        min = 0
    } else if (min < 0) {
        min -= 1
    } else {
        min = -1
    }
    for (i in 0 until 2 * n) {
        intree[i]!!.yd -= min
        intree[i]!!.yt -= min
    }
    val size = max - min
    sz = 1
    while (sz < size) {
        sz *= 2
    }
    tree = arrayOfNulls(2 * sz - 1)
    for (i in 0 until 2 * sz - 1) {
        tree[i] = yNode(0, i, i)
    }
    for (i in sz - 2 downTo 0) {
        tree[i]!!.left = tree[i * 2 + 1]!!.left
        tree[i]!!.right = tree[i * 2 + 2]!!.right
    }
    var res = 0
    var xres = 0
    var yres = 0
    var umax = Int.MIN_VALUE
    for (i in 0 until 2 * n) {
        upd(0, intree[i]!!.yd + sz - 2, intree[i]!!.yt + sz - 2, intree[i]!!.type)
        res = findAns()
        if (umax < tree[0]!!.value) {
            umax = tree[0]!!.value
            xres = intree[i]!!.x
            yres = res - sz + 2 + min
        }
    }
    println(umax)
    println("$xres $yres")
}

fun upd(i: Int, l: Int, r: Int, value: Int) {
    if (i * 2 + 2 >= 2 * sz - 1) {
        if (tree[i]!!.left >= l && tree[i]!!.right <= r) {
            tree[i]!!.value += value
        }
        return
    }
    if (tree[i]!!.right < l || tree[i]!!.left > r) {
        return
    }
    if (tree[i]!!.left >= l && tree[i]!!.right <= r) {
        tree[i]!!.value += value
        return
    }
    push(i)
    upd(i * 2 + 1, l, r, value)
    upd(i * 2 + 2, l, r, value)
    tree[i]!!.value = Integer.max(tree[2 * i + 1]!!.value, tree[2 * i + 2]!!.value)
}

fun push(i: Int) {
    val max = Integer.max(tree[2 * i + 1]!!.value, tree[2 * i + 2]!!.value)
    if (tree[i]!!.value > max) {
        tree[2 * i + 1]!!.value += tree[i]!!.value - max
        tree[2 * i + 2]!!.value += tree[i]!!.value - max
    }
    if (tree[i]!!.value < max) {
        tree[2 * i + 1]!!.value -= max - tree[i]!!.value
        tree[2 * i + 1]!!.value = Integer.max(0, tree[2 * i + 1]!!.value)
        tree[2 * i + 2]!!.value -= max - tree[i]!!.value
        tree[2 * i + 2]!!.value = Integer.max(0, tree[2 * i + 2]!!.value)
    }
}

fun findAns(): Int {
    var res = 0
    while (res * 2 + 2 < 2 * sz - 1) {
        push(res)
        if (tree[res]!!.value == tree[res * 2 + 1]!!.value) {
            res = res * 2 + 1
            continue
        }
        res = res * 2 + 2
    }
    return res
}

class Node(var x: Int, var yt: Int, var yd: Int, var type: Int) : Comparable<Any?> {
    override fun compareTo(arg: Any?): Int {
        if (x > (arg as Node?)!!.x) return 1
        if (x < arg!!.x) return -1
        if (x == arg.x) {
            if (type > arg.type) {
                return -1
            }
            if (type < arg.type) {
                return 1
            }
        }
        return 0
    }
}

class yNode(var value: Int, var left: Int, var right: Int)
