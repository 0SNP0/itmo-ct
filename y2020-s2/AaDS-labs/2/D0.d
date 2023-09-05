import std.stdio;

const DIV = 1_000_000_000L;

long minOf(long a, long b) {
    return a < b ? a : b;
}
long maxOf(long a, long b) {
    return a > b ? a : b;
}

class Node {
    long key, sum, min, max, height;
    Node left, right;

    this(long n) {
        key = min = max = n;
        sum = 0;
        height = 1;
        left = right = null;
    }

    long getLeftHeight() {
        if (left is null) return 0;
        return left.height;
    }
    long getRightHeight() {
        if (right is null) return 0;
        return right.height;
    }
    long getBalance() {
        return getRightHeight() - getLeftHeight();
    }

    long getLeftSum() {
        if (left is null) return 0;
        return left.sum;
    }
    long getRightSum() {
        if (right is null) return 0;
        return right.sum;
    }
    long getLeftMin() {
        if (left is null) return long.max;
        return left.min;
    }
    long getRightMin() {
        if (right is null) return long.max;
        return right.min;
    }
    long getLeftMax() {
        if (left is null) return long.max;
        return left.max;
    }
    long getRightMax() {
        if (right is null) return long.max;
        return right.max;
    }
    void minmaxCalc() {
        min = minOf(minOf(getLeftMin(), getRightMin()), key);
        max = maxOf(maxOf(getLeftMax(), getRightMax()), key);
    }
    void sumCalc() {
        sum = getLeftSum() + getRightSum();
        if (left !is null) sum += left.key;
        if (right !is null) sum += right.key;
    }
    void heightCalc() {
        height = maxOf(this.getLeftHeight(), this.getRightHeight()) + 1;
    }
    void recalcAll() {
        heightCalc(); 
        sumCalc(); 
        minmaxCalc();
    }
}

Node find(Node node, long val) {
    if (node is null || node.key == val) return node;
    if (node.key < val) return find(node.right, val);
    else return find(node.left, val);
}

bool exists(Node node, long val) {
    return node.find(val) !is null;
}

Node rotateRight(Node node) {
    Node l = node.left;
    node.left = l.right;
    l.right = node;
    node.recalcAll();
    l.recalcAll();
    return l;
}
Node rotateLeft(Node node) {
    Node r = node.right;
    node.right = r.left;
    r.left = node;
    node.recalcAll();
    r.recalcAll();
    return r;
}

Node rebalance(Node node) {
    if (node.getBalance() == 2) {
        if (node.right.getBalance() < 0) {
            node.right = rotateRight(node.right);
        }
        return rotateLeft(node);
    }
    if (node.getBalance() == -2) {
        if (node.left.getBalance() > 0) {
            node.left = rotateLeft(node.left);
        }
        return rotateRight(node);
    }
    return node;
}

Node insert(Node node, long key) {
    if (node is null) return new Node(key);
    if (key < node.key) node.left = insert(node.left, key);
    else node.right = insert(node.right, key);
    node.recalcAll();
    return rebalance(node);
}

Node insertNode(Node node, long key) {
    if (node.exists(key)) return node;
    return node.insert(key);
}

long sum(Node node, long l, long r) {
    if (node is null) return 0;
    if (node.key > r) return sum(node.left, l, r);
    if (node.key < l) return sum(node.right, l, r);
    if (node.left is null && node.right is null) return node.key;
    if (l <= node.min && node.max <= r) return node.sum + node.key;
    return sum(node.left, l, r) + sum(node.right, l, r) + node.key;
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
            if (prev == '?') root = root.insertNode((m + res) % DIV);
            else root = root.insertNode(m);
            break;
        case '?':
            long l, r;
            readf(" %d %d", &l, &r);
            res = sum(root, l, r);
            writeln(res);
        }
        prev = c;
    }
}