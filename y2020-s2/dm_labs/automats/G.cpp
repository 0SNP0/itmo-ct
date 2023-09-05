#include <iostream>
#include <vector>
#include <queue>

namespace m {
void open(const std::string & name) {
    freopen((name + ".in").c_str(), "r", stdin);
    freopen((name + ".out").c_str(), "w", stdout);
}
template<class T>
T next(std::istream & in = std::cin) { T res; in >> res; return res; }
using vect_i = std::vector<int>;
using vect_vect_i = std::vector<vect_i>;
using vect_b = std::vector<bool>;
using pair_ii = std::pair<int, int>;
using queue_ii = std::queue<pair_ii>;
}

const std::string NAME = "equivalence";

m::vect_vect_i aut_1, aut_2;
m::vect_i terminal_1, terminal_2;
m::vect_b visited_1, visited_2;

void add_trans(m::vect_vect_i &vect, int a, int b, char c) {
    vect[a][c - 'a'] = b;
}
bool bfs(m::queue_ii queue = m::queue_ii()) {
    queue.push(m::pair_ii(1, 1));
    while (!queue.empty()) {
        auto cur = queue.front();
        if (cur.first != 0) visited_1[cur.first] = true;
        if (cur.second != 0) visited_2[cur.second] = true;
        queue.pop();
        if (terminal_1[cur.first] != terminal_2[cur.second]) return false;
        for (int i = 0; i < 26; i++) {
            auto u = aut_1[cur.first][i], v = aut_2[cur.second][i];
            if (!visited_1[u] || !visited_2[v])
                if (v != 0 || u != 0) {
                    queue.push(m::pair_ii(u, v));
                }
        }
    }
    return true;
}

int main() {
    m::open(NAME);

    int n = m::next<int>(), m = m::next<int>(), k = m::next<int>(); ++n;
    terminal_1.resize((unsigned) n);
    aut_1.resize((unsigned) n);
    visited_1.resize((unsigned) n);
    for (int i = 0; i < n; i++) {
        aut_1[i].resize(26);
    }
    for (int i = 0; i < k; i++) {
        terminal_1[m::next<int>()] = true;
    }
    for (int i = 0; i < m; i++) {
        int a = m::next<int>(), b = m::next<int>(); 
        char c = m::next<char>();
        add_trans(aut_1, a, b, c);
    }

    n = m::next<int>(), m = m::next<int>(), k = m::next<int>(); ++n;
    terminal_2.resize((unsigned) n);
    aut_2.resize((unsigned) n);
    visited_2.resize((unsigned) n);
    for (int i = 0; i < n; i++) {
        aut_2[i].resize(26);
    }
    for (int i = 0; i < k; i++) {
        terminal_2[m::next<int>()] = true;
    }
    for (int i = 0; i < m; i++) {
        int a = m::next<int>(), b = m::next<int>(); 
        char c = m::next<char>();
        add_trans(aut_2, a, b, c);
    }

    std::string ans = bfs() ? "YES" : "NO";
    std::cout << ans;
    return 0;
}