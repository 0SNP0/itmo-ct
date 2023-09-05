#include <iostream>
#include <vector>

std::vector<std::vector<int>> edge;
std::vector<int> decomp;
std::vector<int> lvl;

int count(int node, int size, int &c, int prev) {
    int res = 1;
    for (auto & e: edge[node]) if (lvl[e] == -1 && e != prev)
        res += count(e, size, c, node);
    if ((res * 2 >= size || prev == -1) && c == -1)
        c = node;
    return res;
}
void dfs(int node, int size, int level, int cenroid) {
    int c = -1;
    count(node, size, c, -1);
    lvl[c] = level;
    decomp[c] = cenroid;
    for (auto & e: edge[c]) if (lvl[e] == -1)
        dfs(e, size / 2, level + 1, c);
}

int main() {
    int n;
    std::cin >> n;
    edge.resize(n);
    decomp.resize(n);
    lvl.resize(n, -1);
    for (auto i = 1; i < n; ++i) {
        int a, b;
        std::cin >> a >> b;
        edge[a - 1].push_back(b - 1);
        edge[b - 1].push_back(a - 1);
    }
    dfs(0, n, 0, -1);
    for (auto & e : decomp) {
        std::cout << e + 1 << ' ';
    }
    return 0;
}