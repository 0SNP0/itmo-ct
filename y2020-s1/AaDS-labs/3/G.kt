import java.lang.StringBuilder
import java.util.*

lateinit var d: Array<IntArray>
lateinit var pos: Array<IntArray>
var s: String = ""
var res: StringBuilder? = null

fun main() {
    val scanner = Scanner(System.`in`)
    s = scanner.nextLine()

    val n = s.length
    d = Array(n) { IntArray(n) }
    pos = Array(n) { IntArray(n) }
    res = StringBuilder()
    if (n > 0) {
        for (i in 0 until n) {
            for (j in 0 until n) {
                if (j < i) {
                    d[i][j] = 0
                    continue
                }
                if (i == j) {
                    d[i][j] = 1
                }
            }
        }
        for (r in 0 until n) {
            for (l in r downTo 0) {
                if (l == r) {
                    d[l][r] = 1
                } else {
                    var m = 100000
                    var mk = -10
                    if (s[l] == '(' && s[r] == ')') {
                        m = d[l + 1][r - 1]
                    }
                    if (s[l] == '[' && s[r] == ']') {
                        m = d[l + 1][r - 1]
                    }
                    if (s[l] == '{' && s[r] == '}') {
                        m = d[l + 1][r - 1]
                    }
                    for (k in l until r) {
                        if (m > d[l][k] + d[k + 1][r]) {
                            m = d[l][k] + d[k + 1][r]
                            mk = k
                        }
                    }
                    d[l][r] = m
                    pos[l][r] = mk
                }
            }
        }
        ans(0, n - 1)
        print(res.toString())
    } else {
        print("")
    }
}

fun ans(left: Int, r: Int) {
    if (d[left][r] == r - left + 1) {
        return
    }
    if (d[left][r] == 0) {
        res!!.append(s.substring(left, r + 1))
        return
    }
    if (pos[left][r] == -10) {
        res!!.append(s[left])
        ans(left + 1, r - 1)
        res!!.append(s[r])
        return
    }
    ans(left, pos[left][r])
    ans(pos[left][r] + 1, r)
}