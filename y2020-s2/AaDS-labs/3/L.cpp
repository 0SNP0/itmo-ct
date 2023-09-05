#include <iostream>
#include <fstream>
#include <vector>
#include <queue>
#include <map>
#include <set>
#include <unordered_map>

using llong = long long;

std::vector<std::vector<llong>> edge, centr_arr;
std::vector<llong> decomp, lvl, white_size, black_size, white, black;
std::vector<std::unordered_map<llong, llong>> mv, white_arr, black_arr;
std::vector<bool> color, deleted;

llong dfs_count(llong node, llong size, llong &centroid, llong prev = -1) {
    llong s = 1;
    for (auto & e: edge[node]) if (lvl[e] == -1 && e != prev)
        s += dfs_count(e, size, centroid, node);
    if (centroid == -1 && (s * 2 >= size || prev == -1))
        centroid = node;
    return s;
}
void dfs_create(llong node, llong parent, llong level, llong centroid) {
    mv[centroid][node] = level;
    ++black_size[centroid];
    black[centroid] += level;
    centr_arr[centroid].push_back(node);
    for (auto & e: edge[node]) if (!deleted[e] && e != parent)
        dfs_create(e, node, level + 1, centroid);
}
void dfs(llong node, llong size, llong level, llong cenroid) {
    llong c = -1;
    dfs_count(node, size, c);
    lvl[c] = level;
    decomp[c] = cenroid;
    black[c] = black_size[c] = 0;
    deleted[c] = true;
    dfs_create(c, -1, 0, c);
    for (auto & e: edge[c]) if (lvl[e] == -1)
        dfs(e, size / 2, level + 1, c);
}

void count(llong node) {
    llong centroid = decomp[node];
    if (centroid != -1 && black_arr[centroid].count(node) == 0) {
        black_arr[centroid][node] = 0;
        for (auto & e: centr_arr[node])
            black_arr[centroid][node] += mv[centroid][e];
    }
}
void upd(llong node) {
    if (color[node]) {
        ++black_size[node]; --white_size[node];
        auto prev = node;
        for (auto centroid = decomp[node]; centroid != -1; centroid = decomp[centroid]) {
            ++black_size[centroid]; --white_size[centroid];
            black_arr[centroid][prev] += mv[centroid][node];
            black[centroid] += mv[centroid][node];
            white[centroid] -= mv[centroid][node];
            white_arr[centroid][prev] -= mv[centroid][node];
            prev = centroid;
        }
    } else {
        --black_size[node]; ++white_size[node];
        auto prev = node;
        for (auto centroid = decomp[node]; centroid != -1; centroid = decomp[centroid]) {
            --black_size[centroid]; ++white_size[centroid];
            black_arr[centroid][prev] -= mv[centroid][node];
            black[centroid] -= mv[centroid][node];
            white[centroid] += mv[centroid][node];
            white_arr[centroid][prev] += mv[centroid][node];
            prev = centroid;
        }
    } 
    color[node] = !color[node];
}
llong get(llong node) {
    llong res = 0;
    if (color[node]) {
        auto prev = node;
        res = white[prev];
        for (auto centroid = decomp[node]; centroid != -1; centroid = decomp[centroid]) {
            res -= white_arr[centroid][prev];
            res += white[centroid];
            res += mv[centroid][node] * (white_size[centroid] - white_size[prev]);
            prev = centroid;
        }
    } else {
        auto prev = node;
        res = black[prev];
        for (auto centroid = decomp[node]; centroid != -1; centroid = decomp[centroid]) {
            res -= black_arr[centroid][prev];
            res += black[centroid];
            res += mv[centroid][node] * (black_size[centroid] - black_size[prev]);
            prev = centroid;
        }
    } 
    return res;
}

int main() {
    llong n, m;
    std::cin >> n >> m;
    edge.resize(n);
    decomp.resize(n);
    centr_arr.resize(n);
    lvl.resize(n, -1);
    mv.resize(n);
    white_size.resize(n, 0);
    black_size.resize(n, -1);
    white.resize(n, 0);
    black.resize(n, -1);
    white_arr.resize(n);
    black_arr.resize(n);
    color.resize(n, false);
    deleted.resize(n, false);
    for (llong i = 0; i < n - 1; ++i) {
        llong a, b;
        std::cin >> a >> b;
        edge[a - 1].push_back(b - 1);
        edge[b - 1].push_back(a - 1);
    }
    dfs(0, n, 0, -1);
    for (llong i = 0; i < n; ++i) count(i);
    for (llong i = 0; i < m; ++i) {
        llong type, node;
        std::cin >> type >> node;
        if (type == 1) upd(node - 1);
        else std::cout << get(node - 1) << std::endl;
    }
    return 0;
}
