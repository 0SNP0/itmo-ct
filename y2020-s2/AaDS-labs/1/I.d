import std.stdio;

int R;

struct Matrix {
    int a, b, c, d;

    this(int a, int b, int c, int d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public Matrix mul(Matrix m) {
        return Matrix((a * m.a + b * m.c) % R, (a * m.b + b * m.d) % R, (c * m.a + d * m.c) % R, (c * m.b + d * m.d) % R);
    }

    public void println() {
        writefln("%d %d\n%d %d\n", a, b, c, d);
    }
}

const Matrix NE = Matrix(1, 0, 0, 1);

class ST {
    private {
        Matrix[] tree;
        size_t sz;
    }

    this(Matrix[] arr) {
        for (sz = 1; sz < arr.length; sz *= 2) {}
        tree = new Matrix[sz * 2 - 1];
        tree[sz - 1 .. sz - 1 + arr.length] = arr[0 .. $];
        for (auto i = sz - 1 + arr.length; i < tree.length; ++i) {
            tree[i] = NE;
        }
        for (auto i = cast(int)sz - 2; i >= 0; --i) {
            tree[i] = tree[i * 2 + 1].mul(tree[i * 2 + 2]);
        }
    }

    Matrix get(int l, int r) {
        l += sz - 2, r += sz - 2;
        Matrix lres = NE, rres = NE;
        while (l < r) {
            if (l % 2 == 0) {
                lres = lres.mul(tree[l]);
            }
            l /= 2;
            if (r % 2 == 1) {
                rres = tree[r].mul(rres);
            }
            r = r / 2 - 1;
        }
        if (l == r) {
            lres = lres.mul(tree[l]);
        }
        return lres = lres.mul(rres);
    }
}

void main() {
    int n, m;
    readf(" %d %d %d", &R, &n, &m);
    auto arr = new Matrix[n];
    foreach (ref e; arr) {
        int a, b, c, d;
        readf(" %d %d %d %d", &a, &b, &c, &d);
        e = Matrix(a, b, c, d);
    }
    auto st = new ST(arr);
    for (; m > 0; --m) {
        int l, r;
        readf(" %d %d", &l, &r);
        st.get(l, r).println();
    }
}