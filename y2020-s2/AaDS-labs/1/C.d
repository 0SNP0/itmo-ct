import std.stdio;

struct Node (T) {
    T all;
    T max;
    T mpre;
    T mpost;
    this (T sum, T m, T pre, T post) {
        all = sum;
        max = m;
        mpre = pre;
        mpost = post;
    }
    this (T n) {
        this(n, n, n, n);
    }
}

class STMaxSum (T) {
    
    private {
        Node!T[] tree;
        size_t sz;
        Node!T NE;
    }

    this(T[] arr, T min = 1L << 63) {
        NE = Node!T(min);
        for (sz = 1; sz < arr.length; sz *= 2) {}
        tree = new Node!T[sz * 2 - 1];
        for (auto i = 0; i < arr.length; ++i) {
            tree[sz - 1 + i] = Node!T(arr[i]);
        }
        for (auto i = sz - 1 + arr.length; i < tree.length; ++i) {
            tree[i] = NE;
        }
        for (auto i = cast(int)sz - 2; i >= 0; --i) {
            tree[i] = op(tree[i * 2 + 1], tree[i * 2 + 2]);
        }
    }

    private Node!T op(Node!T l, Node!T r) {
        if (r == NE) {
            return l;
        }
        return Node!T(l.all + r.all, maxOf(l.max, r.max, l.mpost + r.mpre), 
                    maxOf(l.mpre, l.all + r.mpre), maxOf(r.mpost, r.all + l.mpost));
    }

    public void set(size_t i, T item) {
        i += sz - 1;
        tree[i] = Node!T(item);
        while (i > 0) {
            i = (i - 1) / 2;
            tree[i] = op(tree[i * 2 + 1], tree[i * 2 + 2]);
        }
    }

    public T get() {
        return maxOf(tree[0].max, 0);
    }
    
    T maxOf(T a, T b) {
        return a > b ? a : b;
    }
    T maxOf(T a, T b, T c) {
        return maxOf(a, maxOf(b, c));
    }
}

void main() {
    int n, m;
    readf("%d %d", &n, &m);
    auto arr = new long[n];
    foreach (ref e; arr) {
        readf(" %d", &e);
    }
    auto st = new STMaxSum!long(arr);
    writeln(st.get());
    for (; m > 0; --m) {
        size_t i;
        long v;
        readf(" %d %d", &i, &v);
        st.set(i, v);
        writeln(st.get());
    }
}