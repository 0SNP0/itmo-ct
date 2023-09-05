import std.stdio;
import std.random;

class Node {
    int y, size, val;
    Node left, right;
    this(int v) {
        y = Random(unpredictableSeed).front;
        size = 1;
        val = v;
        left = right = null;
    }
}

int getSize(Node t) {
    return t is null ? 0 : t.size;
}

void updSize(Node t) {
    if (t is null) return;
    t.size = t.left.getSize() + t.right.getSize() + 1;
}

Node merge(Node a, Node b) {
    if (a is null) return b;
    if (b is null) return a;
    if (a.y > b.y) {
        a.right = merge(a.right, b);
        a.updSize();
        return a;
    } else {
        b.left = merge(a, b.left);
        b.updSize();
        return b;
    }
}

void split(Node t, int x, ref Node a, ref Node b) {
    if (t is null) {
        a = b = null;
        return;
    }
    if (t.left.getSize() < x) {
        t.right.split(x - getSize(t.left) - 1, t.right, b);
        a = t;
    } else {
        t.left.split(x, a, t.left);
        b = t;
    }
    t.updSize();
}

Node add(Node t, int pos, int v) {
    Node a, b;
    t.split(pos, a, b);
    return a.merge(new Node(v)).merge(b);
}

Node remove(Node t, int pos) {
    Node a, b, c, d;
    t.split(pos, a, b);
    b.split(1, c, d);
    destroy(c);
    return a.merge(d);
}

Node treeFromArray(int[] arr) {
    Node res;
    foreach (ref v; arr) {
        res = res.merge(new Node(v));
    }
    return res;
}

void print(Node tree) {
    if (tree is null) return;
    tree.left.print();
    "%d ".writef(tree.val);
    tree.right.print();
}

//----------------------------------------

void main() {
    int n, m;
    string act;
    " %d %d".readf(&n, &m);
    int[] arr = new int[n];
    foreach (ref e; arr)
        " %d".readf(&e);
    Node tree = treeFromArray(arr);
    // print(tree); writeln();
    for (; m > 0; --m) {
        " %s ".readf(&act);
        final switch (act) {
        case "del":
            int i;
            " %d".readf(&i);
            tree = tree.remove(i - 1);
            // print(tree); writeln();
            continue;
        case "add":
            int i, v;
            " %d %d".readf(&i, &v);
            tree = tree.add(i, v);
            // print(tree); writeln();
            continue;
        }
    }
    writeln(tree.getSize());
    print(tree);
}