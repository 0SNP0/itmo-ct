import std.stdio;

class ST (T) {

    private {
        T[] tree;
        size_t len;
        size_t sz = 1;
    }

    this(size_t n) {
        while (this.sz < n) {
            this.sz *= 2;
        }
        this.tree = new T[2 * sz - 1];
        this.len = n;
    }

    this(T[] arr) {
        this(arr.length);
        this.build(arr);
    }
    
    private T op(T a, T b) {
        return a + b;
    }
    private T ch(T a, T b) {
        return a - b;
    }

    public void build(T[] arr) {
        tree[sz - 1 .. sz - 1 + arr.length] = arr[0 .. $];
        for (auto i = cast(int)sz - 2; i >= 0; --i) {
            tree[i] = op(tree[i * 2 + 1], tree[i * 2 + 2]);
        }
    }

    public void set(size_t i, T item) {
        i = sz - 1 + i;
        T change = ch(item, tree[i]);
        tree[i] = item;
        while (i > 0) {
            i = (i - 1) / 2;
            tree[i] = op(tree[i], change);
        }
    }

    public T get(size_t l, size_t r) {
        l += sz - 1, r += sz - 2;
        T lres = 0, rres = 0;
        while (l < r) {
            if (l % 2 == 0) {
                lres = op(lres, tree[l]);
            }
            l /= 2;
            if (r % 2 == 1) {
                rres = op(rres, tree[r]);
            }
            r = r / 2 - 1;
        }
        if (l == r) {
            lres = op(lres, tree[l]);
        }
        return op(lres, rres);
    }
}

void main() {
    int n, m;
    readf("%d %d", &n, &m);
    auto arr = new long[n];
    foreach (ref e; arr) {
        readf(" %d", &e);
    }
    auto st = new ST!long(arr);
    for (; m > 0; --m) {
        readf(" %d", &n);
        final switch (n) {
        case 1:
            size_t i;
            long v;
            readf(" %d %d", &i, &v);
            st.set(i, v);
            break;
        case 2:
            size_t l, r;
            readf(" %d %d", &l, &r);
            writeln(st.get(l, r));
            break;
        }
    }
}