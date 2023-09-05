import std.stdio;
import std.algorithm;

const MIN = (1 << 31);
const MAX = (1 << 31) - 1;

int k;
int size;
Node[] tree;

struct Node {
    int left;
    int right;
    int lb;
    int rb;
    int color;
    int children;
    int num;
}

int sum(int a, int b) {
    if (a != MIN && b != MIN) {
        return a + b;
    } else if (a != MIN) {
        return a;
    } else if (b != MIN) {
        return b;
    } else {
        return MIN;
    }
}

void upd(int i, int left, int right, int val) {
    if (i * 2 + 2 >= 2 * k - 1) {
        if (tree[i].left >= left && tree[i].right <= right) {
            tree[i].color = val * tree[i].children;
            if (val == 1) {
                tree[i].num = 1;
                tree[i].lb = tree[i].left;
                tree[i].rb = tree[i].right;
            } else {
                tree[i].num = 0;
                tree[i].lb = tree[i].rb = 0;
            }
        }
        return;
    }
    if (tree[i].right < left || tree[i].left > right) {
        return;
    }
    if (tree[i].left >= left && tree[i].right <= right) {
        tree[i].color = val * tree[i].children;
        if (val == 1) {
            tree[i].num = 1;
            tree[i].lb = tree[i].left;
            tree[i].rb = tree[i].right;
        } else {
            tree[i].num = 0;
            tree[i].lb = tree[i].rb = 0;
        }
        return;
    }
    if (tree[i].color == tree[i].children) {
        tree[2 * i + 1].color = tree[2 * i + 1].children;
        tree[2 * i + 2].color = tree[2 * i + 2].children;
        tree[2 * i + 1].num = tree[i].num;
        tree[2 * i + 2].num = tree[i].num;
        tree[2 * i + 1].lb = tree[2 * i + 1].left;
        tree[2 * i + 1].rb = tree[2 * i + 1].right;
        tree[2 * i + 2].lb = tree[2 * i + 2].left;
        tree[2 * i + 2].rb = tree[2 * i + 2].right;
        tree[i].color = MIN;
    }
    if (tree[i].color == 0) {
        tree[2 * i + 1].color = 0;
        tree[2 * i + 2].color = 0;
        tree[2 * i + 1].num = 0;
        tree[2 * i + 2].num = 0;
        tree[2 * i + 1].lb = 0;
        tree[2 * i + 1].rb = 0;
        tree[2 * i + 2].lb = 0;
        tree[2 * i + 2].rb = 0;
        tree[i].color = MIN;
    }
    upd(i * 2 + 1, left, right, val);
    upd(i * 2 + 2, left, right, val);
    tree[i].color = sum(tree[2 * i + 1].color, tree[2 * i + 2].color);
    tree[i].lb = tree[2 * i + 1].lb != 0? tree[2 * i + 1].lb: tree[2 * i + 2].lb;
    tree[i].rb = tree[2 * i + 2].rb != 0? tree[2 * i + 2].rb: tree[2 * i + 1].rb;
    if (tree[2 * i + 2].lb - tree[2 * i + 1].rb == 1) {
        tree[i].num = sum(tree[2 * i + 1].num, tree[2 * i + 2].num) - 1;
    } else {
        tree[i].num = sum(tree[2 * i + 1].num, tree[2 * i + 2].num);
    }
}

void main() {
    int n;
    readf(" %d", &n);
    char[] input = new char[n];
    int[] left = new int[n];
    int[] right = new int[n];
    int mn = MAX;
    int mx = MIN;
    for (auto i = 0; i < n; ++i) {
        readf(" %c", &input[i]);
        readf(" %d", &left[i]);
        readf(" %d", &right[i]);
        mn = left[i] < mn? left[i] : mn;
    }
    for (auto i = 0; i < n; ++i) {
        if (left[i] < 0) {
            left[i] -= min(0, mn);
        } else {
            left[i] -= min(-1, mn);
        }
        right[i] += left[i];
        mx = mx < right[i] ? right[i] : mx;
        right[i] -= 1;
    }
    size = mx;
    for (k = 1; k < size; k *= 2) {}
    tree = new Node[2 * k - 1];
    for (auto i = 0; i < 2 * k - 1; i++) {
        tree[i].color = MIN;
        tree[i].left = tree[i].right = i;
        tree[i].lb = tree[i].rb = tree[i].num = 0;
        tree[i].children = 1;
    }
    for (auto i = k - 2; i >= 0; --i) {
        tree[i].left = tree[i * 2 + 1].left;
        tree[i].right = tree[i * 2 + 2].right;
        tree[i].children = tree[2 * i + 1].children + tree[2 * i + 2].children;
    }
    for (auto i = 0; i < n; ++i) {
        if (input[i] == 'B') {
            upd(0, left[i] + k - 1, right[i] + k - 1, 1);
            writefln("%d %d", tree[0].num, tree[0].color);
        } else {
            upd(0, left[i] + k - 1, right[i] + k - 1, 0);
            writefln("%d %d", tree[0].num, tree[0].color);
        }
    }
}