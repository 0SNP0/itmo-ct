import std.stdio;
import std.conv;

void readD(T...)(ref T args) { foreach (ref e; args) readf(" %d", &e); }
void readS(T...)(ref T args) { foreach (ref e; args) readf(" %s ", &e); }
int nextInt() { int r; readf(" %d", &r); return r; }
struct Pair(A, B) {
    public: A first; B second;
    this(ref A a, ref B b) { first = a; second = b; }
    @property ref A f() { return first; }
    @property ref A f(A a) { return first = a; }
    @property ref B s() { return second; }
    @property ref B s(B b) { return second = b; }
}
Pair!(A, B) pair(A, B)(A a, B b) { return Pair!(A, B)(a, b); }
T min(T)(ref T a, ref T b) { return a < b ? a : b; }
T max(T)(ref T a, ref T b) { return a > b ? a : b; }
void swap(T)(ref T a, ref T b) { const tmp = a; a = b; b = tmp; }

class Node {
    public:
    Node left = null, right = null, parent = null;
    int cnt = 1, val = 1;
    Pair!(int, int) key = pair(-1, -1);
    this(Node p, ref const Pair!(int, int) k, ref const int v) {
        parent = p;
        key = k;
        val = v;
    }
}

long[Pair!(int, int)] priority;
Node[Pair!(int, int)] tree;

int count(Node node) { return node is null ? 0 : node.cnt; }
void countUpd(ref Node node) { if (node !is null) node.cnt = 1 + node.left.count() + node.right.count(); }
int get(Node node) {
    auto res = node.left.count();
    for (; node !is null; node = node.parent) if (node.parent !is null && node.parent.right == node) {
        res += 1 + node.parent.left.count();
    }
    return res;
}
Node getRoot(Node node) {
    for (; node !is null; node = node.parent) {}
    return node;
}
Node merge(ref Node l, ref Node r) {
    if (l is null) return r;
    if (r is null) return l;
    if (priority[l.key] > priority[r.key]) {
        l.right = merge(l.right, r);
        if (l.right) l.right.parent = l;
        l.countUpd();
        l.parent = null;
        return l;
    } else {
        r.left = merge(l, r.left);
        if (r.left) r.left.parent = r;
        r.countUpd();
        r.parent = null;
        return r;
    }
}
Pair!(Node, Node) split(ref Node root, int x) {
    if (root is null) return Pair!(Node, Node)(null, null);
    const l = root.left.count();
    if (l >= x) {
        Pair!(Node, Node) tmp = split(root.left, x);
        root.left = tmp.second;
        if (root.left) root.left.parent = root;
        root.countUpd();
        if (root !is null) root.parent = null;
        if (tmp.first !is null) tmp.first.parent = null;
        return pair(tmp.first, root);
    } else {
        Pair!(Node, Node) tmp = split(root.right, x - l - 1);
        root.right = tmp.first;
        if (root.right !is null) root.right.parent = root;
        root.countUpd();
        if (root !is null) root.parent = null;
        if (tmp.second !is null) tmp.second.parent = null;
        return pair(root, tmp.second);
    }
}
void link(int v, int u) {
    Node vu = new Node(null, pair(v, u), 0);
    Node uv = new Node(null, pair(u, v), 0);
    tree[pair(v, u)] = vu;
    tree[pair(u, v)] = uv;
    toPriority[pair(u, v)] = to!long(to!string(u) + to!string(v));
    toPriority[pair(v, u)] = to!long(to!string(v) + to!string(u));
    const fdlg = getRoot(tree[pair(u, u)]);
    const ge = get(tree[pair(u, u)]);
    Pair!(Node, Node) B = split(getRoot(tree[pair(u, u)]), get(tree[pair(u, u)]));
    Pair!(Node, Node) A = split(getRoot(tree[pair(v, v)]), get(tree[pair(v, v)]));
    Node *tmp = merge(A.first, vu);
    tmp = merge(tmp, B.second);
    tmp = merge(tmp, B.first);
    tmp = merge(tmp, uv);
    tmp = merge(tmp, A.second);
}
void cut(int v, int u) {
    Node vu = toTree[pair(v, u)];
    Node uv = toTree[pair(u, v)];
    if (get(uv) < get(vu)) swap(vu, uv);
    Pair!(Node, Node) tmp1 = split(getRoot(vu), getX(vu));
    Pair!(Node, Node) tmp2 = split(tmp1.second, getX(vu) + 1);
    Pair!(Node, Node) tmp3 = split(getRoot(uv), getX(uv));
    Pair!(Node, Node) tmp4 = split(tmp3.second, getX(uv) + 1);
    merge(tmp1.first, tmp4.second);
}
int connected(int v, int u) {
    return getRoot(tree[pair(v, v)]) == getRoot(tree[pair(u, u)]) ? 1 : 0;
}

void main() {
    const int n, m;
    readD(n, m);
    for (auto i = 0; i < n; ++i) {
        Node tmp = new Node(null, pair(i, i), 1);
        priority[pair(i, i)] = to!long(to!string(i) + to!string(i));
        tree[pair(i, i)] = tmp;
    }
    string s;
    int v, u;
    for (int i = 0; i < m; ++i) {
        readS(s);
        readD(v, u); --v; --u;
        final switch (s) {
        case "link":
            link(min(v, u), max(v, u));
            break;
        case "cut":
            cut(min(v, u), max(v, u));
            break;
        case "connected":
            writeln(connected(min(v, u), max(v, u)));
            break;
        }
    }
}