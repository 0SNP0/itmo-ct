import std.stdio;

const INF = 1000000;
int[][] d;
int[] a;

int dp(int i, int j) {
    if (j > i) {
        return INF;
    } else {
        int res;
        int cost = a[i];
        if (j <= 0) {
            if (i >= 1) {
                if (cost <= 100) {
                    int m1 = dp(i - 1, j + 1), m2 = dp(i - 1, j) + cost;
                    res = m1 < m2 ? m1 : m2;
                } else {
                    return dp(i - 1, j + 1);
                }
            } else {
                return 0;
            }
        } else {
            if (d[i][j] != -1) {
                return d[i][j];
            }
            if (cost > 100) {
                int m1 = dp(i - 1, j + 1), m2 = dp(i - 1, j - 1) + cost;
                res = m1 < m2 ? m1 : m2;
            } else {
                int m1 = dp(i - 1, j + 1), m2 = dp(i - 1, j) + cost;
                    res = m1 < m2 ? m1 : m2;
            }
        }
        d[i][j] = res;
        return res;
    }
}

void g(Stack!int used, int i, int j) {
    if (j < i) {
        int cost = a[i];
        if (j <= 0) {
            if (i >= 1) {
                if (cost > 100) {
                    used.add(i);
                    g(used, i - 1, j + 1);
                } else {
                    bool addi = (dp(i, j) == dp(i - 1, j + 1));
                    if (addi) {
                        used.add(i);
                        g(used, i - 1, j + 1);
                    } else {
                        g(used, i - 1, j);
                    }
                }
            }
        } else {
            if (cost <= 100) {
                bool addi = (dp(i - 1, j + 1) == dp(i, j));
                if (addi) {
                    used.add(i);
                    g(used, i - 1, j + 1);
                } else {
                    g(used, i - 1, j);
                }
            } else {
                bool addi = (dp(i - 1, j + 1) == dp(i, j));
                if (addi) {
                    used.add(i);
                    g(used, i - 1, j + 1);
                } else {
                    g(used, i - 1, j - 1);
                }
            }
        }
    }
}

void main() {
    int n, k1, k2;
    readf(" %d", &n);
    a = new int[n + 1];
    for (auto i = 1; i <= n; i++) {
        readf(" %d", &a[i]);
    }
    d = new int[][](n + 1, n + 2);
    for (auto i = 0; i <= n; i++) {
        for (int j = 0; j <= n + 1; j++) {
            d[i][j] = -1;
        }
    }
    int ans = INF;
    for (int j = 0; j <= n; j++) {
        if (ans >= dp(n, j)) {
            ans = dp(n, j);
            k1 = j;
        }
    }
    writeln(ans);
    Stack!int used = new Stack!int();
    g(used, n, k1);
    k2 = used.length();
    writefln("%d %d", k1, k2);
    while (!used.isEmpty()) {
        writeln(used.removeLast());
    }
}

class Stack(T) {
    private {
        T[] arr;
        int size;
    }

    this() {
        arr = new T[1];
        size = 0;
    }

    private void resize() {
        if (size >= arr.length) {
            T[] buf = arr;
            arr = new T[](arr.length * 2);
            arr[0..size] = buf[];
        }
    }

    int length() {
        return size;
    }

    void add(T n) {
        resize();
        arr[size++] = n;
    }

    T removeLast() {
        return arr[--size];
    }

    bool isEmpty() {
        return size == 0;
    }
}