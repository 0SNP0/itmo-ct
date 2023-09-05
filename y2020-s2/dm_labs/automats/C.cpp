#include <fstream>
#include <vector>

template<class T>
T next(std::istream & in) { T res; in >> res; return res; }

const std::string NAME = "problem3";
static constexpr long long MOD = 1e9 + 7;

std::vector<std::vector<int>> aut;
std::vector<std::vector<int>> aut_rev;
std::vector<bool> terminal;
std::vector<bool> visited;
std::vector<bool> used;

void add_trans(int a, int b) {
    aut[a].push_back(b);
    aut_rev[b].push_back(a);
}
long long count(int v) {
    if (!used[v]) return 0;
    auto res = terminal[v] ? 1LL : 0LL;
    for (auto i = 0LL; i < aut[v].size(); ++i) {
        res = (res + count(aut[v][i])) % MOD;
    }
    return res;
}
void dfs(int v) {
    if (used[v] || visited[v]) return;
    used[v] = true;
    visited[v] = true;
    for (auto i = 0; i < aut_rev[v].size(); ++i)
        dfs(aut_rev[v][i]);
    visited[v] = false;
}
bool has_cycle(int v) {
    if (!used[v]) return false;
    if (visited[v]) return true;
    visited[v] = true;
    for (auto i = 0; i < aut[v].size(); ++i)
        if (has_cycle(aut[v][i])) {
            visited[v] = false;
            return true;
        }
    visited[v] = false;
    return false;
}

int main() {
    std::ifstream in(NAME + ".in");
    std::ofstream out(NAME + ".out");
    std::vector<int> term_states;
    int n = next<int>(in), m = next<int>(in), k = next<int>(in);
    aut.resize(n + 1); aut_rev.resize(n + 1);
    terminal.resize(n + 1); visited.resize(n + 1); used.resize(n + 1);
    for (; k; --k) {
        int x = next<int>(in);
        terminal[x] = true;
        term_states.push_back(x);
    };
    for (; m; --m) {
        int a = next<int>(in), b = next<int>(in);
        next<char>(in);
        add_trans(a, b);
    }
    for (auto & e : term_states) {
        dfs(e);
    }
    out << (has_cycle(1) ? -1 : count(1));
    return 0;
}
