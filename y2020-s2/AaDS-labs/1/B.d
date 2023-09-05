import std.stdio;

class Node (T) {
    T item;
    int count;
    this(T item, int count) {
        this.item = item;
        this.count = count;
    }
}

class STMC (T) {

    private {
        Node!T[] tree;
        size_t n;
        size_t sz;
        Node!T INF;
    }

    this(T[] arr, T inf = (1L << 63) - 1) {
        INF = new Node!T(inf, 1);
        n = arr.length;
        for (sz = 1; sz < n; sz *= 2) {}
        tree = new Node!T[sz * 2 - 1];
        for (auto i = 0; i < n; ++i) {
            tree[sz - 1 + i] = new Node!T(arr[i], 1);
        }
        for (auto i = sz - 1 + n; i < tree.length; ++i) {
            tree[i] = INF;
        }
        for (auto i = cast(int)sz - 2; i >= 0; --i) {
            tree[i] = op(tree[i * 2 + 1], tree[i * 2 + 2]);
        }
    }

    private Node!T op(Node!T l, Node!T r) {
        if (l.item == r.item) {
            return new Node!T(l.item, l.count + r.count);
        } else if (l.item < r.item) {
            return l;
        } else {
            return r;
        }
    }

    public void set(size_t i, T item) {
        i += sz - 1;
        tree[i] = new Node!T(item, 1);
        while (i > 0) {
            i = (i - 1) / 2;
            tree[i] = op(tree[i * 2 + 1], tree[i * 2 + 2]);
        }
    }

    public Node!T get(size_t l, size_t r) {
        l += sz - 1, r += sz - 2;
        Node!T lres = INF, rres = INF;
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
    auto st = new STMC!long(arr);
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
            Node!long res = st.get(l, r);
            writefln("%d %d", res.item, res.count);
            break;
        }
    }
}