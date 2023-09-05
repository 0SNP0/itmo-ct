#include <iostream>
#include <vector>
#include <map>

namespace m {
    void open(const std::string & name) {
        freopen((name + ".in").c_str(), "r", stdin);
        freopen((name + ".out").c_str(), "w", stdout);
    }
    template<class T>
    T next(std::istream & in = std::cin) { T res; in >> res; return res; }
}

const std::string NAME = "problem4";
static constexpr int MOD = 1e9 + 7;

namespace dsm {
    std::vector<bool> terminal;
    std::vector<std::vector<int>> trans;
    void resize(size_t n) {
        terminal.resize(n);
        trans.resize(n);
    }
}

int D_solve(int l) {
    std::map<int, int> map({{0, 1}});
    for (; l; --l) {
        std::map<int, int> new_map;
        for (const auto & p : map) 
            for (const auto & to : dsm::trans.at(p.first))
                new_map[to] = (new_map[to] + p.second) % MOD;
        map = new_map;
    }
    auto res = 0;
    for(const auto & p : map) {
        if (dsm::terminal[p.first]) res = (res + p.second) % MOD;
    }
    return res;
}
void D() {
    int n = m::next<int>(), m = m::next<int>(), k = m::next<int>(), l = m::next<int>();
    dsm::resize(n);
    for (; k; --k) 
        dsm::terminal[m::next<int>() - 1] = true;
    for (; m; --m) {
        int a = m::next<int>() - 1, b = m::next<int>() - 1; m::next<char>();
        dsm::trans[a].push_back(b);
    }
    std::cout << D_solve(l);
}
int main() {
    m::open(NAME);
    D();
    return 0;
}