import std.stdio;
import std.random;

// alias rand = Alias!(Random(unpredictableSeed).front);

class Node {
    int size, p, data;
    bool zero;
    Node left, right;
    this(int data = 0, bool zero = true) {
        this.data = data;
        this.zero = zero;
        p = Random(unpredictableSeed).front * Random(unpredictableSeed).front;
        size = 1;
        left = right = null;
    }
}

class Treap {
    Node root = null;
    int num = 0;

    this(int n) {
        for (auto i = 0; i < n; ++i) {
            root = this.merge(new Node(), root);
        }
    }

    Node merge(Node l, Node r) {
        if (l is null) return r;
        if (r is null) return l;
        if (l.p > r.p) {
            l.right = merge(l.right, r);
            sizeUpd(l);
            zeroUpd(l);
            return l;
        } else {
            r.left = merge(l, r.left);
            sizeUpd(r);
            zeroUpd(r);
            return r;
        }
    }

    void split(Node t, int key, ref Node l, ref Node r) {
        if (t is null) {
            l = r = null;
            return;
        }
        if (sizeGet(t.left) < key) {
            split(t.right, key - sizeGet(t.left) - 1, t.right, r);
            l = t;
        } else {
            split(t.left, key, l, t.left);
            r = t;
        }
        sizeUpd(t);
        zeroUpd(t);
    }

    int sizeGet(Node t) {
        return (t is null) ? 0 : t.size;
    }

    void sizeUpd(Node t) {
        t.size = sizeGet(t.left) + sizeGet(t.right) + 1;
    }

    bool zeroGet(Node t) {
        return (t is null) ? false : t.zero;
    }

    void zeroUpd(Node t) {
        t.zero = zeroGet(t.left) || zeroGet(t.right) || t.data == 0;
    }

    Node nullSearch(Node t, ref int i) {
        i = sizeGet(t.left);
        while (zeroGet(t)) {
            if (t.left !is null && zeroGet(t.left)) {
                t = t.left;
                i -= (sizeGet(t.right) + 1);
            } else if (dataGet(t) == 0) {
                break;
            } else {
                t = t.right;
                i += sizeGet(t.left) + 1;
            }
        }
        return t;
    }

    int dataGet(Node t) {
        if (t is null) return -1;
        return t.data;
    }

    void toArray(Node t, ref int[] arr) {
        if (t is null) return;
        toArray(t.left, arr);
        arr ~= dataGet(t);
        toArray(t.right, arr);
    }

    Node remove(Node node, int key) {
        Node t1, t2, t3;
        split(node, key, t1, t2);
        split(t2, 1, t2, t3);
        node = merge(t1, t3);
        destroy(t2);
        return node;
    }

    void insert(int id) {
        Node z, l, r;
        int i;
        split(root, id, l, r);
        z = nullSearch(r, i);
        if (z !is null && z.data == 0) {
            r = remove(r, i);
        }
        root = merge(merge(l, new Node(++num, false)), r);
    }
}

void main() {
    int n, m, j;
    readf(" %d %d", &n, &m);
    Treap tree = new Treap(m);
    for (auto i = 0; i < n; ++i) {
        readf(" %d", &j);
        tree.insert(j - 1);
    }
    int[] res;
    tree.toArray(tree.root, res);
    for (; res.length > 0 && res[$-1] == 0; res = res[0..($-1)]) {}
    writef("%d\n", res.length);
    for (auto i = 0; i < res.length; i++) {
        writef("%d ", res[i]);
    }
    // writeln();
}