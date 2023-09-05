#include <iostream>
#include <map>
#include <queue>
#include <set>
#include <vector>

namespace m {
    void open(const std::string & name) {
        freopen((name + ".in").c_str(), "r", stdin);
        freopen((name + ".out").c_str(), "w", stdout);
    }
    template<typename T>
    T next(std::istream & in = std::cin) { T res; in >> res; return res; }

    using VectB = std::vector<bool>;
    using VectI = std::vector<int>;
    using VectVI = std::vector<VectI>;
    using VectVVI = std::vector<VectVI>;
    using SetI = std::set<int>;
    using VectSI = std::vector<SetI>;
    using MapSB = std::map<SetI, bool>;
    using MapSV = std::map<SetI, VectSI>;
    using QueueSI = std::queue<SetI>;
    using MapSI = std::map<SetI, int>;
}

const std::string NAME = "problem5";
static constexpr int MOD = 1e9 + 7;
static constexpr int TC = 'z' - 'a' + 1;

namespace nfsm {
    m::VectB terminal;
    m::VectVVI trans;
    void resize(size_t n) {
        terminal.resize(n);
        trans.resize(n, m::VectVI(TC));
    }
}

int E_solve(int l) {
    m::MapSB used;
    m::MapSV trans;
    m::QueueSI queue({m::SetI{0}});
    used[queue.front()] = true;
    while (!queue.empty()) {
        auto from = queue.front();
        queue.pop();
        trans[from].resize(TC);
        for (auto c = 0; c < TC; ++c) {
            m::SetI to;
            for (const auto & e : from)
                for (const auto & t : nfsm::trans[e][c])
                    to.insert(t);
            if (!used[to]) {
                queue.push(to);
                used[to] = true;
            }
            trans[from][c] = to;
        }
    }
    m::MapSI map({{{0}, 1}});
    for (; l; --l) {
        m::MapSI new_map;
        for (const auto & p : map)
            for (const auto & to : trans[p.first])
                new_map[to] = (new_map[to] + p.second) % MOD;
        map = new_map;
    }
    auto res = 0;
    for (const auto & p : map) {
        bool lt = false;
        for (const auto & s : p.first)
            lt = lt || nfsm::terminal[s];
        if (lt) res = (res + p.second) % MOD;
    }
    return res;
}
void E() {
    int n = m::next<int>(), m = m::next<int>(), k = m::next<int>(), l = m::next<int>();
    nfsm::resize(n);
    for (; k; --k) 
        nfsm::terminal[m::next<int>() - 1] = true;
    for (; m; --m) {
        int a = m::next<int>() - 1, b = m::next<int>() - 1, c = m::next<char>() - 'a';
        nfsm::trans[a][c].push_back(b);
    }
    std::cout << E_solve(l);
}
int main() {
    m::open(NAME);
    E();
    return 0;
}