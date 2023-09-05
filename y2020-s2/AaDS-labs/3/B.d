import std.stdio;

void readD(T...)(ref T args) { foreach (ref e; args) readf(" %d", &e); }
int nextInt() { int r; readf(" %d", &r); return r; }

int n, l, counter = 0;
int[] tree_in, tree_out;
int[][] vect;
int[][] up;

bool upper(ref const int l, ref const int r) {
    return tree_in[l] <= tree_in[r] && tree_out[l] >= tree_out[r];
}
void dfs(const int node, const int parent) {
    int to;
    tree_in[node] = counter++;
    up[node][0] = parent;
    for (auto i = 1; i <= l; ++i)
        up[node][i] = up[up[node][i - 1]][i - 1];
    for (auto i = 0; i < vect[node].length; ++i) {
        to = vect[node][i];
        if (to != parent)
            dfs(to, node); // @suppress(dscanner.confusing.argument_parameter_mismatch)
    }
    tree_out[node] = counter++;
}
int lca(int a, int b) {
    if (a.upper(b)) return a;
    if (b.upper(a)) return b;
    for (auto i = l; i >= 0; --i)
        if (!up[a][i].upper(b))
            a = up[a][i];
    return up[a][0];
}

void main() {
    readD(n);
    for (l = 1; 1 << l <= n; ++l) {}
    vect = new int[][n + 1];
    up = new int[][](n + 1, l + 1);
    tree_in = new int[n + 1];
    tree_out = new int[n + 1];
    for (auto i = 2; i < n + 1; ++i) {
        vect[nextInt()] ~= i;
    }
    dfs(1, 1);
    const m = nextInt();
    for (auto i = 0; i < m; ++i) {
        writeln(lca(nextInt(), nextInt()));
    }
}
