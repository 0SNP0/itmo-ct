import std.stdio;
import std.random;

class Node {
    int x, y, size;
    Node left, right;
    this(int k) {
        x = k;
        y = Random(unpredictableSeed).front;
        size = 1;
        left = right = null;
    }
}

int getSize(Node t) {
    return t is null ? 0 : t.size;
}

void updSize(Node node) {
    if (node is null) return;
    node.size = node.left.getSize() + node.right.getSize() + 1;
}

Node merge(Node l, Node r) {
    if (l is null) return r;
    if (r is null) return l;
    if (l.y > r.y) {
        l.right = merge(l.right, r);
        l.updSize();
        return l;
    } else {
        r.left = merge(l, r.left);
        r.updSize();
        return r;
    }
}

void split(Node t, int x, ref Node l, ref Node r) {
    if (t is null) {
        l = r = null;
        return;
    }
    if (getSize(t.left) < x) {
        split(t.right, x - getSize(t.left) - 1, t.right, r);
        l = t;
    } else {
        split(t.left, x, l, t.left);
        r = t;
    }
    t.updSize();
}

Node buildTree(int[] arr) {
    Node res;
    for (auto i = 0; i < arr.length; ++i) {
        res = merge(res, new Node(arr[i]));
    }
    return res;
}

Node toStart(Node t, int l, int r) {
    Node a, b, c, d;
    split(t, r + 1, a, b);
    split(a, l, c, d);
    return merge(merge(d, c), b);
}

void print(Node tree) {
    if (tree is null) return;
    tree.left.print();
    "%d ".writef(tree.x);
    tree.right.print();
}

void main() {
    int n, m, l, r;
    int[] arr;
    " %d %d".readf(&n, &m);
    for (auto i = 1; i <= n; ++i) {
        arr ~= i;
    }
    Node tree = buildTree(arr);
    for (auto i = 0; i < m; ++i) {
        " %d %d".readf(&l, &r);
        tree.toStart(l - 1, r - 1);
    }
    tree.print();
}