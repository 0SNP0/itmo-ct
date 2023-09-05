import std.stdio;

struct Node (T) {
    T min, add;

    this(T m, T a) {
        min = m;
        add = a;
    }

    public void inc(T n) {
        min += n;
        add += n;
    }
}

class STAM (T) {
    private {
        Node!T[] tree;
        size_t sz;
    }

    this(size_t len) {
        for (sz = 1; sz < len; sz *= 2) {}
        tree = new Node!T[sz * 2 - 1];
    }

    private T op(T a, T b) {
        return a < b ? a : b;
    }

    private void upd(size_t i) {
        tree[i * 2 + 1].inc(tree[i].add);
        tree[i * 2 + 2].inc(tree[i].add);
        tree[i].add = 0;
    }

    public void add(size_t l, size_t r, T v) {
        l += sz - 1, r += sz - 2;
        while (l < r) {
            if (l % 2 == 0) {
                tree[l].inc(v);
                upd(l / 2 - 1);
                tree[l / 2 - 1].min = op(tree[l - 1].min, tree[l].min);
            }
            l /= 2;
            if (r % 2 == 1) {
                tree[r].inc(v);
                upd(l / 2);
                tree[l / 2].min = op(tree[l].min, tree[l + 1].min);
            }
            r = r / 2 - 1;
        }
        if (l == r) {
            tree[l].inc(v);
            if (l > 0) {
                l = (l - 1) / 2;
                upd(l);
                tree[l].min = op(tree[l * 2 + 1].min, tree[l * 2 + 2].min);
            }
        }
    }

    public T getMin(size_t l, size_t r) {
        l += sz - 1, r += sz - 2;
        T lres = 1 << 30, rres = 1 << 30;
        while (l < r) {
            if (l % 2 == 0) {
                upd(l / 2 - 1);
                lres = op(lres, tree[l].min);
            } else {
                lres += tree[l / 2 - 1].add;
            }
            l /= 2;
            if (r % 2 == 1) {
                upd(r / 2);
                rres = op(rres, tree[r].min);
            } else {
                rres += tree[l / 2].add;
            }
            r = r / 2 - 1;
        }
        while (l != r) {
            lres += tree[r].add;
            l = (l - 1) / 2;
            rres += tree[l].add;
            r = (r - 1) / 2;
        }
        lres = op(lres, rres);
        while (l > 0) {
            lres += tree[l].add;
            l = (l - 1) / 2;
        }
        return lres + tree[0].add;
    }
}

void main() {
    int n, m;
    readf("%d %d", &n, &m);
    auto st = new STAM!int(n);
    for (; m > 0; --m) {
        readf(" %d", &n);
        final switch (n) {
        case 1:
            size_t l, r;
            int v;
            readf(" %d %d %d", &l, &r, &v);
            st.add(l, r, v);
            break;
        case 2:
            size_t l, r;
            readf(" %d %d", &l, &r);
            writeln(st.getMin(l, r));
            break;
        }
    }
}