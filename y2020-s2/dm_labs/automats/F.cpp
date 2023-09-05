#include <iostream>
#include <vector>

namespace m {
    void open(const std::string & name) {
        freopen((name + ".in").c_str(), "r", stdin);
        freopen((name + ".out").c_str(), "w", stdout);
    }
    template<typename T>
    T next(std::istream & in = std::cin) { T res; in >> res; return res; }
}

const std::string NAME = "isomorphism";

namespace dsm {
    static constexpr int TC = 'z' - 'a' + 1;
    std::pair<std::vector<bool>, std::vector<bool>> term;
    std::vector<bool> used;
    std::pair<std::vector<std::vector<int>>, std::vector<std::vector<int>>> trans;
    void resize(const size_t & n) {
        term.first.resize(n); term.second.resize(n);
        trans.first.resize(n, std::vector<int>(TC, -1));
        trans.second.resize(n, std::vector<int>(TC, -1));
        used.resize(n);
    }
    bool dfs(int, int);
}

std::string result(bool r) { return r ? "YES" : "NO"; }
void F() {
    int n = m::next<int>(), m = m::next<int>(), k = m::next<int>();
    dsm::resize(n);
    for (auto i = 0; i < k; ++i)
        dsm::term.first[m::next<int>() - 1] = true;
    for (auto i = 0; i < m; ++i) {
        int a = m::next<int>() - 1, b = m::next<int>() - 1, c = m::next<char>() - 'a';
        dsm::trans.first[a][c] = b;
    }
    int n1 = m::next<int>(), m1 = m::next<int>(), k1 = m::next<int>();
    if (n1 != n || m1 != m || k1 != k) {
        std::cout << result(false);
        return;
    }
    for (auto i = 0; i < k; ++i)
        dsm::term.second[m::next<int>() - 1] = true;
    for (auto i = 0; i < m; ++i) {
        int a = m::next<int>() - 1, b = m::next<int>() - 1, c = m::next<char>() - 'a';
        dsm::trans.second[a][c] = b;
    }
    std::cout << result(dsm::dfs(0, 0));
}
int main() {
    m::open(NAME);
    F();
    return 0;
}

bool dsm::dfs(int a, int b) {
    used[a] = true;
    if (term.first[a] ^ term.second[b]) return false;
    for (auto c = 0; c < TC; ++c) {
        auto to = std::make_pair(trans.first[a][c], trans.second[b][c]);
        if (to.first < 0 && to.second < 0) continue;
        if (((to.first < 0) ^ (to.second < 0)) || (!used[to.first] && !dfs(to.first, to.second)))
            return false;
    }
    return true;
}