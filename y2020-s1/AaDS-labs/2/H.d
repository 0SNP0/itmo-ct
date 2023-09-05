import std.stdio;

class DSU {

    private {
        int[] arr, c;
        int r;
    }

    this(int n, int m) {
        arr = new int[n + m];
        c = new int[n + m];
        for (int i = 0; i < n + m; ++i) {
            arr[i] = i;
        }
        r = n;
    }
 
    private int root(int n) {
        while (arr[n] != n) {
            n = arr[n];
        }
        return n;
    }

    void join(int a, int b) {
        int ra = root(--a), rb = root(--b);
        if (ra == rb) return;
        arr[ra] = r;
        arr[rb] = r;
        r++;
    }

    void add(int n, int v) {
        c[root(n - 1)] += v;
    }

    int get(int n) {
        int sum = c[--n];
        while (arr[n] != n) {
            n = arr[n];
            sum += c[n];
        }
        return sum;
    }
}

void main() {
    int n, m;
    readf(" %d", &n);
    readf(" %d", &m);
    auto dsu = new DSU(n, m);
    for (; m; --m) {
        string s;
        readf(" %s ", &s);
        if (s == "add") {
            int x, v;
            readf(" %d %d", &x, &v);
            dsu.add(x, v);
        } else if (s == "join") {
            int x, y;
            readf(" %d %d", &x, &y);
            dsu.join(x, y);
        } else {
            int x;
            readf(" %d", &x);
            writeln(dsu.get(x));
        }
    }
}