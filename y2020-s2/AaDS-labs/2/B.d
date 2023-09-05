import std.stdio;
import std.random;

const DIV = 1_000_000_000;
class Node {
    long x;
    int y;
    Node l, r;
    this(long x) {
        this.x = x;
        this.y = Random(unpredictableSeed).front;
        this.l = null;
        this.r = null;
    }
}

void split(Node tree, ref Node l, ref Node r, long x) {
    if (tree is null) {
        l = null;
        r = null;
        return;
    }
    if (tree.x < x) {
        split(tree.r, tree.r, r, x);
        l = tree;
    } else {
        split(tree.l, l, tree.l, x);
        r = tree;
    }
}

void merge(ref Node tree, Node l, Node r) {
    if (l is null || r is null) {
        if (l is null) tree = r;
        else tree = l;
        return;
    }
    if (l.y > r.y) {
        merge(l.r, l.r, r);
        tree = l;
    } else {
        merge(r.l, l, r.l);
        tree = r;
    }
}

void insert(ref Node tree, Node v) {
    if (tree is null) {
        tree = v;
        return;
    }
    if (tree.y > v.y) {
        if (v.x < tree.x)
            insert(tree.l, v);
        else insert(tree.r, v);
        return;
    }
    split(tree, v.l, v.r, v.x);
    tree = v;
}

void remove(ref Node tree, long x) {
    if (tree is null) return;
    if (x == tree.x)  merge(tree, tree.l, tree.r);
    else if (x < tree.x) remove(tree.l, x);
    else if (x > tree.x) remove(tree.r, x);
}

bool exists(ref Node tree, long x) {
    if (tree is null) return false;
    if (x < tree.x) return exists(tree.l, x);
    else if (x > tree.x) return exists(tree.r, x);
    else return true;
}

Node prev(Node c, long x) {
    Node s = null;
    while (c !is null) {
        if (c.x < x) {
            s = c;
            c = c.r;
        } else {
            c = c.l;
        }
    }
    return s;
}

Node next(Node c, long x) {
    Node s = null;
    while (c !is null) {
        if (c.x > x) {
            s = c;
            c = c.l;
        } else {
            c = c.r;
        }
    }
    return s;
}

void main() {
    string s;
    long x;
    Node tree = null;
    while (readf(" %s %d ", &s, &x)) {
        final switch (s) {
        case "insert":
            if(!tree.exists(x)) tree.insert(new Node(x));
            continue;
        case "delete":
            if(tree.exists(x)) tree.remove(x);
            continue;
        case "exists":
            writeln(tree.exists(x));
            continue;
        case "next":
            auto res = tree.next(x);
            if (res is null) writeln("none");
            else writeln(res.x);
            continue;
        case "prev":
            auto res = tree.prev(x);
            if (res is null) writeln("none");
            else writeln(res.x);
            continue;
        }
    }
}