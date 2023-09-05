#include <iostream>
#include <algorithm>
#include <vector>
#include <queue>
void open_files(const std::string & name) {
    freopen((name + ".in").c_str(), "r", stdin);
    freopen((name + ".out").c_str(), "w", stdout);
}
template <typename T>
T next(std::istream & in = std::cin) { T res; in >> res; return std::move(res); }
std::string nextline(std::istream & in = std::cin) { std::string res; std::getline(in, res); return std::move(res); }
#define FOR(n) for (auto __i = 0; __i < n; ++__i)
#define SORT(t) std::sort(t.begin(), t.end())
#define CONTAINS(t, e) (std::find(t.begin(), t.end(), e) != t.end())
#define PUSH_BACK_ONCE(t, e) if (!CONTAINS(t, e)) t.push_back(e)
using uint = unsigned int;

static const std::string NAME = "useless";
static constexpr int SC = 'z' - 'a' + 1;

class C {
    void solve(std::queue<char> &);
public:
    C();
private:
    uint n, start;
    std::vector<uint> cnt;
    std::vector<bool> gen;
    std::vector<std::vector<uint>> rls;
    std::vector<bool> used;
    std::vector<char> noterm;
    std::vector<std::pair<char, std::string>> rule;
};

C::C() : n(next<uint>()), cnt(n), gen(SC), rls(SC), used(SC) {
    auto c = next<char>();
    noterm.push_back(c);
    start = c - 'A';
    std::queue<char> queue;
    FOR(n) {
        c = next<char>();
        PUSH_BACK_ONCE(noterm, c);
        next<std::string>();
        auto s = nextline();
        if (!gen.at(c - 'A')) {
            gen.at(c - 'A') = true;
            queue.push(c);
        }
        for (const auto & e : s) if ('A' <= e && e <= 'Z')
            PUSH_BACK_ONCE(noterm, e);
        rule.emplace_back(c, s.length() ? s.substr(1) : "");
    }
    solve(queue);
    SORT(noterm);
    for (size_t i = 0; i < noterm.size(); ++i)
        if (!gen.at(noterm.at(i) - 'A') || !used.at(noterm.at(i) - 'A'))
            std::cout << noterm.at(i) << ' ';
}
int main() {
    open_files(NAME);
    C();
    return 0;
}
void C::solve(std::queue<char> & queue) {
    while (!queue.empty()) {
        auto cur = queue.front() - 'A'; queue.pop();
        for (auto i = 0; i < rls.at(cur).size(); ++i) {
            if (cnt.at(rls.at(cur).at(i)) > 0)
                cnt.at(rls.at(cur).at(i))--;
            if (!cnt.at(rls.at(cur).at(i)) && !gen.at(rule.at(rls.at(cur).at(i)).first - 'A')) {
                gen.at(rule.at(rls.at(cur).at(i)).first - 'A') = true;
                queue.push(rule.at(rls.at(cur).at(i)).first);
            }
        }
    }
    std::vector<std::vector<unsigned int>> edges(SC);
    for (auto i = 0; i < rule.size(); ++i)
            for (size_t j = 0; j < rule.at(i).second.size(); ++j)
                if (rule.at(i).second.at(j) >= 'A' && rule.at(i).second.at(j) <= 'Z')
                    edges.at(rule.at(i).first - 'A').push_back(rule.at(i).second.at(j) - 'A');
    std::queue<uint> q;
    if (gen.at(start)) {
        q.push(start);
        used.at(start) = true;
    }
    while (!q.empty()) {
        unsigned int cur = q.front(); q.pop();
        for (size_t i = 0; i < edges.at(cur).size(); ++i)
            if (!used.at(edges.at(cur).at(i))) {
                used.at(edges.at(cur).at(i)) = true;
                q.push(edges.at(cur).at(i));
            }
    }
}