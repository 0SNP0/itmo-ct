import java.util.Scanner

fun check(x: Long, y: Long, n: Int): Boolean {
    val a = LongArray(5)
    for (i in 0 until n - 1) {
        when {
            x and (1 shl i).toLong() == 0L -> a[1] = 0
            else -> a[1] = 1
        }
        when {
            x and (1 shl i + 1).toLong() == 0L -> a[2] = 0
            else -> a[2] = 1
        }
        when {
            y and (1 shl i).toLong() == 0L -> a[3] = 0
            else -> a[3] = 1
        }
        when {
            y and (1 shl i + 1).toLong() == 0L -> a[4] = 0
            else -> a[4] = 1
        }
        if (a[1] == a[2] && a[2] == a[3] && a[3] == a[4]) {
            return false
        }
    }
    return true
}

fun patternCount(n: Int, m: Int): Long {
    var result = 0L
    val len = 1 shl n
    val a = Array(m) { LongArray(len) }
    val d = Array(len) { LongArray(len) }
    for (i in 0 until len) {
        for (j in 0 until len) {
            if (check(i.toLong(), j.toLong(), n)) {
                d[i][j] = 1
            } else {
                d[i][j] = 0
            }
        }
    }
    for (i in 0 until len) {
        a[0][i] = 1
    }
    for (x in 1 until m) {
        for (i in 0 until len) {
            for (j in 0 until len) {
                a[x][i] = a[x][i] + a[x - 1][j] * d[j][i]
            }
        }
    }
    for (i in 0 until len) {
        result += a[m - 1][i]
    }
    return result
}

fun main() {
    val sc = Scanner(System.`in`)
    var n = sc.nextInt()
    var m = sc.nextInt()
    if (n > m) {
        val tmp = n
        n = m
        m = tmp
    }
    print(patternCount(n, m))
}