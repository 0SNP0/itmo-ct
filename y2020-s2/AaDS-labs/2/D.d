import std.stdio;
import std.random;

const DIV = 1_000_000_000;

struct Pair (A, B) {
    private A a;
    private B b;
    this(A a, B b) {
        this.a = a;
        this.b = b;
    }
    @property first() {return a;}
    @property second() {return b;}
}

class Node {
    long x, y, sum;
    Node left, right;
    this(long key) {
        x = sum = key;
        y = Random(unpredictableSeed).front;
        left = right = null;
    }
}

long sum(Node t) {
    return t is null ? 0 : t.sum;
}

void upd(Node t) {
    t.sum = sum(t.left) + sum(t.right) + t.x;
}

Pair!(Node, Node) split(Node t, long k) {
    if (t is null) return Pair!(Node, Node)(null, null);
    if (k > t.x) {
        Pair!(Node, Node) tmp = split(t.right, k);
        t.right = tmp.first;
        t.upd();
        return Pair!(Node, Node)(t, tmp.second);
    }
    Pair!(Node, Node) tmp = split(t.left, k);
    t.left = tmp.second;
    t.upd();
    return Pair!(Node, Node)(tmp.first, t);
}

Node merge(Node l, Node r) {
    if (l is null) return r;
    if (r is null) return l;
    if (l.y > r.y) {
        l.right = merge(l.right, r);
        l.upd();
        return l;
    } else {
        r.left = merge(l, r.left);
        r.upd();
        return r;
    }
}

Node find(Node t, long k) {
    if (t is null || t.x == k) {
        return t;
    } else if (k < t.x) {
        return find(t.left, k);
    } else {
        return find(t.right, k);
    }
}

bool exists(Node t, long k) {
    return t.find(k) !is null;
}

void insert(ref Node root, long x) {
    if (root.exists(x)) return;
    Pair!(Node, Node) tmp = split(root, x);
    root = merge(tmp.first, merge(new Node(x), tmp.second));
}

long findSum(ref Node root, long l, long r) {
    Pair!(Node, Node) t1 = split(root, l);
    Pair!(Node, Node) t2 = split(t1.second, r + 1);
    const res = sum(t2.first);
    root = merge(t1.first, merge(t2.first, t2.second));
    return res;
}

void main() {
    Node root;
    char prev = '+';
    long res = 0;
    long n;
    readf(" %d", &n);
    for (long i = 0; i < n; ++i) {
        char c;
        readf(" %c", &c);
        final switch (c) {
        case '+':
            long m;
            readf(" %d", &m);
            if (prev == '?') root.insert((m + res) % DIV);
            else root.insert(m);
            break;
        case '?':
            long l, r;
            readf(" %d %d", &l, &r);
            res = root.findSum(l, r);
            writeln(res);
            break;
        }
        prev = c;
    }
}