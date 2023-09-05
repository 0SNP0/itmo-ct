import std.stdio;

class ST (T) {

    private {
        T[] tree;
        size_t sz;
        T NE;
    }

    this(T[] arr, T min = 1L << 63) {
        NE = min;
        for (sz = 1; sz < arr.length; sz *= 2) {}
        tree = new T[sz * 2 - 1];
        tree[sz - 1 .. sz - 1 + arr.length] = arr[0 .. $];
        for (auto i = sz - 1 + arr.length; i < tree.length; ++i) {
            tree[i] = NE;
        }
        for (auto i = cast(int)sz - 2; i >= 0; --i) {
            tree[i] = this.op(tree[i * 2 + 1], tree[i * 2 + 2]);
        }
    }

    private T op(T a, T b) {
        return a > b ? a : b;
    }

    public void set(size_t i, T item) {
        i += sz - 1;
        tree[i] = item;
        while (i > 0) {
            i = (i - 1) / 2;
            tree[i] = op(tree[i * 2 + 1], tree[i * 2 + 2]);
        }
    }

    public T get(size_t l, T x) {
        auto lim = sz * 2;
        for (int i = cast(int) (l + sz - 1); i < lim - 1; i /= 2) {
            if (tree[i] >= x) {
                while (i < sz - 1) {
                    if (tree[i * 2 + 1] >= x) {
                        i = i * 2 + 1;
                    } else {
                        i = i * 2 + 2;
                    }
                }
                return i + 1 - sz;
            }
            lim /= 2;
        }
        return -1;
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
            long x;
            size_t l;
            readf(" %d %d", &x, &l);
            writeln(st.get(l, x));
            break;
        }
    }
}