import std.stdio;
import std.random;

const MIN = -1_000_000_001;

class Node {
    int x, y, size;
    Node left, right;
    this(int n) {
        x = n;
        y = Random(unpredictableSeed).front;
        size = 1;
        left = right = null;
    }
}

int getSize(Node node) {
    return node is null ? 0 : node.size;
}
void updSize(Node node) {
    if (node is null) return;
    node.size = node.left.getSize() + node.right.getSize() + 1;
}

void split(Node t, ref Node l, ref Node r, int x) {
    if (t is null) {
        l = null;
        r = null;
        return;
    } 
    if (t.x > x) {
        split(t.left, l, t.left, x);
        r = t;
    } else {
        split(t.right, t.right, r, x);
        l = t;
    }
    t.updSize();
}

void merge(ref Node t, Node l, Node r) {
    if (l is null || r is null) {
        if (l is null) t = r;
        else t = l;
        return;
    }
    if (l.y > r.y) {
        merge(l.right, l.right, r);
        t = l;
    } else {
        merge(r.left, l, r.left);
        t = r;
    }
    t.updSize();
}


void insert(ref Node tree, Node v) {
    if (tree is null) {
        tree = v;
        return;
    }
    if (tree.y < v.y) {
        split(tree, v.left, v.right, v.x);
        tree = v;
    } else {
        insert(v.x < tree.x ? tree.left : tree.right, v);
    }
    tree.updSize();
}

void remove(ref Node tree, long x) {
    if (tree is null) return;
    if (x == tree.x)  merge(tree, tree.left, tree.right);
    else if (x < tree.x) remove(tree.left, x);
    else if (x > tree.x) remove(tree.right, x);
    tree.updSize();
}

int findKthMax(ref Node tree, int k) {
    const int sizeleft = getSize(tree.left);
    if (sizeleft == k) {
        return tree.x;
    } else if (sizeleft > k)
        return findKthMax(tree.left, k);
    else {
        k -= sizeleft + 1;
        return findKthMax(tree.right, k);
    }
}

void main() {
    int n, c, k, len = 0;
    Node tree;
    readf(" %d", &n);
    for (auto i = 0; i < n; ++i) {
        readf(" %d %d", &c, &k);
        final switch (c) {
        case (1):
            tree.insert(new Node(k));
            ++len;
            continue;
        case 0:
            tree.findKthMax(len - k).writeln();
            continue;
        case -1:
            tree.remove(k);
            --len;
            continue;
        }
    }
}