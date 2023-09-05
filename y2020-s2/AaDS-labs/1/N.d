import std.stdio;

class ST3D (T) {
    private {
        T[][][] tree;
        size_t sz;
        size_t len;
    }

    this(size_t n) {
        for (sz = 1; sz < n; sz *= 2) {}
        len = 2 * sz - 1;
        tree = new T[][][](len, len, len);
    }

    private T op(T a, T b) {
        return a + b;
    }

    private void updZ(const size_t x, const size_t y, size_t z, T change) {
        while (z > 0) {
            z = (z - 1) / 2;
            tree[x][y][z] = op(tree[x][y][z], change);
        }
    }

    private void updY(const size_t x, size_t y, size_t z, T change) {
        updZ(x, y, z, change);
        while (y > 0) {
            y = (y - 1) / 2;
            tree[x][y][z] = op(tree[x][y][z], change);
            updZ(x, y, z, change);
        }
    }

    public void add(size_t x, size_t y, size_t z, T change) {
        x += sz - 1;
        y += sz - 1;
        z += sz - 1;
        tree[x][y][z] = op(tree[x][y][z], change);
        updY(x, y, z, change);
        while (x > 0) {
            x = (x - 1) / 2;
            tree[x][y][z] = op(tree[x][y][z], change);
            updY(x, y, z, change);
        }
    }

    private T getZ(const size_t x, const size_t y, size_t lz, size_t rz) {
        T lres = 0, rres = 0;
        while (lz < rz) {
            if (lz % 2 == 0) {
                lres = op(lres, tree[x][y][lz]);
            }
            lz /= 2;
            if (rz % 2 == 1) {
                rres = op(rres, tree[x][y][rz]);
            }
            rz = rz / 2 - 1;
        }
        if (lz == rz) {
            lres = op(lres, tree[x][y][lz]);
        }
        return op(lres, rres);
    }

    private T getY(const size_t x, size_t ly, size_t ry, const size_t lz, const size_t rz) {
        T lres = 0, rres = 0;
        while (ly < ry) {
            if (ly % 2 == 0) {
                lres = op(lres, getZ(x, ly, lz, rz));
            }
            ly /= 2;
            if (ry % 2 == 1) {
                rres = op(rres, getZ(x, ry, lz, rz));
            }
            ry = ry / 2 - 1;
        }
        if (ly == ry) {
            lres = op(lres, getZ(x, ly, lz, rz));
        }
        return op(lres, rres);
    }

    public T get(size_t lx, size_t ly, size_t lz, size_t rx, size_t ry, size_t rz) {
        lx += sz - 1, rx += sz - 1, ly += sz - 1, ry += sz - 1, lz += sz - 1, rz += sz - 1;
        T lres = 0, rres = 0;
        while (lx < rx) {
            if (lx % 2 == 0) {
                lres = op(lres, getY(lx, ly, ry, lz, rz));
            }
            lx /= 2;
            if (rx % 2 == 1) {
                rres = op(rres, getY(rx, ly, ry, lz, rz));
            }
            rx = rx / 2 - 1;
        }
        if (lx == rx) {
            lres = op(lres, getY(lx, ly, ry, lz, rz));
        }
        return op(lres, rres);
    }
}

void main() {
    int n;
    readf(" %d", &n);
    auto st = new ST3D!long(n);
    while(readf(" %d", &n))  {
        final switch (n) {
        case 1:
            size_t x, y, z;
            long k;
            readf(" %d %d %d %d", &x, &y, &z, &k);
            st.add(x, y, z, k);
            break;
        case 2:
            size_t x1, y1, z1, x2, y2, z2;
            readf(" %d %d %d %d %d %d", &x1, &y1, &z1, &x2, &y2, &z2);
            writeln(st.get(x1, y1, z1, x2, y2, z2));
            break;
        case 3:
            return;
        }
    }
}