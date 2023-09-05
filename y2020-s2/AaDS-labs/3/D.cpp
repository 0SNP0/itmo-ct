#include <iostream>
#include <numeric>
#include <vector>

int n, l;
std::vector<std::vector<int>> edge, up;
std::vector<int> t_in, t_out, parent, lvl, counter, tree, alive;
std::vector<bool> dead;

void dfs(int v, int p, int d) {
    up[v][0] = p;
    lvl[v] = d;
    for (int i = 1; i <= l; ++i) up[v][i] = up[up[v][i - 1]][i - 1];
    for (auto & e: edge[v]) dfs(e, v, d + 1);
}
void set(int node) {
    parent[node] = node;
    tree[node] = 0;
}
int find(int node) {
    if (node == parent[node]) return node;
    return parent[node] = find(parent[node]);
}
void unite(int a, int b) {
    a = find(a);
    b = find(b);
    if (a != b) {
        if (tree[a] < tree[b]) {
            int tmp = a;
            a = b;
            b = tmp;
        }
        parent[b] = a;
        if (tree[a] == tree[b])
            ++tree[a];
    }
}
void upd(int node) {
    set(node);
    for (auto child: edge[node]) if (dead[child])
        unite(child, node);
    alive[find(node)] = up[node][0];
    if (dead[up[node][0]]) {
        int tmp = alive[find(up[node][0])];
        unite(node, up[node][0]);
        alive[find(node)] = tmp;
    }
}
int get(int node) {
    if (dead[node]) return alive[find(node)];
    return node;
}

int lca(int a, int b) {
    if (lvl[a] > lvl[b]) {
        int tmp = b;
        b = a;
        a = tmp;
    }
    for (auto i = l; i >= 0; --i) if (lvl[b] - lvl[a] >= counter[i])
        b = up[b][i];
    if (a == b) return a;
    for (auto i = l; i >= 0; --i) 
        if (up[a][i] != up[b][i] && lvl[up[a][i]] == lvl[up[b][i]]) {
            a = up[a][i];
            b = up[b][i];
    }
    return get(up[a][0]);
}
void insert(int parent, int child) {
    lvl[child] = lvl[parent] + 1;
    up[child][0] = parent;
    for (auto i = 1; i <= l; ++i)
        up[child][i] = up[up[child][i - 1]][i - 1];
}

int main() {
    std::cin >> n;
    for (l = 1; (1 << l) <= n; ++l);
    edge.resize(n + 1);
    dead.resize(n + 1, false);
    up.resize(n + 1, std::vector<int>(l + 1));
    t_in.resize(n + 1);
    t_out.resize(n + 1);
    parent.resize(n + 1);
    lvl.resize(n + 1);
    tree.resize(n + 1);
    alive.resize(n + 1);
    std::iota(alive.begin(), alive.end(), 0);
    for (int i = 0; i <= l; ++i) counter.push_back(1 << i);
    dfs(0, 0, 1);
    int number = 1;
    for (int i = 0; i < n; ++i) {
        std::string s;
        std::cin >> s;
        if (s == "+") {
            int a;
            std::cin >> a;
            edge[a - 1].push_back(number);
            insert(a - 1, number++);
        } else if (s == "-") {
            int a;
            std::cin >> a;
            dead[a - 1] = true;
            upd(a - 1);
        } else {
            int a, b;
            std::cin >> a >> b;
            std::cout << lca(a - 1, b - 1) + 1 << std::endl;
        }
    }
    return 0;
}
