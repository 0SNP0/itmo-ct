import std.stdio;

class STBool {

    private {
        int[] tree;
        size_t sz;
    }

    this(int[] arr) {
        for (sz = 1; sz < arr.length; sz *= 2) {}
        tree = new int[sz * 2 - 1];
        tree[sz - 1 .. sz - 1 + arr.length] = arr[0 .. $];
        for (auto i = cast(int)sz - 2; i >= 0; --i) {
            tree[i] = tree[i * 2 + 1] + tree[i * 2 + 2];
        }
    }

    public void change(size_t i) {
        i = sz - 1 + i;
        const ch = tree[i] ? -1 : 1;
        tree[i] += ch;
        while (i > 0) {
            i = (i - 1) / 2;
            tree[i] += ch;
        }
    }

    public size_t find(size_t n) {
        size_t i = 0;
        while (i < sz - 1) {
            if (n < tree[i * 2 + 1]) {
                i = i * 2 + 1;
            } else {
                n -= tree[i * 2 + 1];
                i = i * 2 + 2;
            }
        }
        return i + 1 - sz;
    }
}

void main() {
    int n, m;
    readf("%d %d", &n, &m);
    auto arr = new int[n];
    foreach (ref e; arr) {
        readf(" %d", &e);
    }
    auto st = new STBool(arr);
    for (; m > 0; --m) {
        readf(" %d", &n);
        final switch (n) {
        case 1:
            size_t i;
            readf(" %d", &i);
            st.change(i);
            break;
        case 2:
            size_t k;
            readf(" %d", &k);
            writeln(st.find(k));
            break;
        }
    }
}