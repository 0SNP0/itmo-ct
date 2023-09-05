import std.stdio;

void readD(T...)(ref T args) { foreach (ref e; args) readf(" %d", &e); }
int nextInt() { int r; readf(" %d", &r); return r; }
struct Pair(A, B) {
    public: A first; B second;
    this(ref const A a, ref const B b) { first = a; second = b; }
    @property ref A f() { return first; }
    @property ref A f(A a) { return first = a; }
    @property ref B s() { return second; }
    @property ref B s(B b) { return second = b; }
}
Pair!(A, B) pair(A, B)(const A a, const B b) { return Pair!(A, B)(a, b); }
T min(T)(ref T a, ref T b) { return a < b ? a : b; }

int n, l, cnt;
int[] tree_in, tree_out;
Pair!(int, int)[][] vect;
Pair!(int, int)[][] up;

bool upper(ref const int l, ref const int r) {
    return tree_in[l] <= tree_in[r] && tree_out[l] >= tree_out[r];
}
void dfs(const int node, const int parent, const int node1) {
    tree_in[node] = cnt++;
    up[node][0] = pair(parent, node1);
    for (int i = 1; i <= l; ++i) {
        up[node][i].first = up[up[node][i - 1].first][i - 1].first;
        up[node][i].second = min(up[node][i - 1].second, up[up[node][i - 1].first][i - 1].second);
    }
    for (int i = 0; i < vect[node].length; ++i) {
        dfs(vect[node][i].first, node, vect[node][i].second);
    }
    tree_out[node] = cnt++;
}
int lca_min(int a, int b) {
    int res = int.max;
    for (auto i = l; i >= 0; i--)
        if (!up[a][i].first.upper(b)) {
            res = min(res, up[a][i].second);
            a = up[a][i].first;
        }
    if (!a.upper(b))
        res = min(res, up[a][0].second);
    for (auto i = l; i >= 0; --i)
        if (!up[b][i].first.upper(a)) {
            res = min(res, up[b][i].second);
            b = up[b][i].first;
        }
    if (!b.upper(a))
        res = min(res, up[b][0].second);
    return res;
}

void main() {
    readD(n);
    for (l = 1; 1 << l <= n; ++l) {}
    vect = new Pair!(int, int)[][n + 1];
    up = new Pair!(int, int)[][](n + 1, l + 1);
    tree_in = new int[n + 1];
    tree_out = new int[n + 1];
    for (auto i = 2; i <= n; ++i) {
        const x = nextInt();
        vect[x] ~= pair(i, nextInt());
    }
    dfs(1, 1, int.max);
    const m = nextInt();
    for (auto i = 0; i < m; ++i) {
        writeln(lca_min(nextInt(), nextInt()));
    }
}