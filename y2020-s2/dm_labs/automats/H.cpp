#include <iostream>
#include <queue>

void open(const std::string & name) {
    freopen((name + ".in").c_str(), "r", stdin);
    freopen((name + ".out").c_str(), "w", stdout);
}
template<typename T>
T next(std::istream & in = std::cin) { T res; in >> res; return res; }

const std::string NAME = "minimization";
static constexpr int TC = 'z' - 'a' + 1;

std::vector<bool> term, n_term;
std::vector<std::vector<int>> trans, n_trans;
std::vector<std::vector<std::vector<int>>> trans_rev;
std::vector<bool> used;
std::vector<std::vector<bool>> mark;
void dsm_resize(int && n) {
    term.resize(n); n_term.resize(n);
    trans.resize(n, std::vector<int>(TC)); n_trans.resize(n, std::vector<int>(TC));
    trans_rev.resize(n, std::vector<std::vector<int>>(TC));
    used.resize(n);
    mark.resize(n, std::vector<bool>(n));
}

void dfs(int n) {
    used[n] = true;
    for (auto & to : trans[n])
		if (!used[to])
			dfs(to);
}

void H() {
    int n = next<int>(), m = next<int>(), k = next<int>();
    dsm_resize(n + 1);
    for (size_t i = 0; i < k; ++i) {
        term[next<int>()] = true;
    }
    for (size_t i = 0; i < m; ++i) {
        int a = next<int>(), b = next<int>(), c = next<char>() - 'a';
        trans[a][c] = b;
        trans_rev[b][c].push_back(a);
    }
    dfs(1);
    std::queue<std::pair<int, int>> queue;
    for (auto i = 0; i <= n; ++i) {
        for (auto j = 0; j <= n; ++j) {
            if (!mark[i][j] && term[i] ^ term[j]) {
                mark[i][j] = mark[j][i] = true;
                queue.push({i, j});
            }
        }
    }
    while (!queue.empty()) {
        auto pair = queue.front();
        queue.pop();
        for (auto c = 0; c < TC; ++c) 
            for (auto i = 0; i < trans_rev[pair.first][c].size(); ++i) 
                for (auto j = 0; j < trans_rev[pair.second][c].size(); ++j) 
                    if (!mark[trans_rev[pair.first][c][i]][trans_rev[pair.second][c][j]]) {
                        mark[trans_rev[pair.first][c][i]][trans_rev[pair.second][c][j]] = mark[trans_rev[pair.second][c][j]][trans_rev[pair.first][c][i]] = true;
                        queue.push(std::make_pair(trans_rev[pair.first][c][i], trans_rev[pair.second][c][j]));
                    }
    }
    std::vector<int> comp(n + 1, -1);
    for (auto i = 0; i <= n; ++i)
        if (!mark[0][i])
            comp[i] = 0;
    int n1 = 0, m1 = 0, k1 = 0;
    for (auto i = 1; i <= n; ++i) {
        if (!used[i]) continue;
        if (comp[i] < 0) {
            comp[i] = ++n1;
            for (auto j = i + 1; j <= n; j++)
                if (!mark[i][j])
                    comp[j] = n1;
        }
    }
    for (auto i = 1; i <= n; ++i)
        for (auto c = 0; c < TC; ++c)
            if (trans[i][c] != 0 && comp[trans[i][c]] != 0)
                n_trans[comp[i]][c] = comp[trans[i][c]];
    for (auto i = 1; i <= n1; ++i) 
        for (auto j = 0; j < TC; ++j) 
            if (n_trans[i][j])
                ++m1;   
    for (auto i = 1; i <= n; ++i)
        if (term[i])
            n_term[comp[i]] = true;
    for (auto i = 1; i <= n1; ++i)
        if (n_term[i])
            ++k1;
    std::cout << n1 << ' ' << m1 << ' ' << k1 << '\n';
    for (auto i = 1; i <= n1; ++i)
        if (n_term[i])
            std::cout << i << ' ';
    std::cout << '\n';
    for (auto i = 1; i <= n1; ++i)
        for (auto j = 0; j < TC; ++j)
            if (n_trans[i][j]) std::cout << i << ' ' << n_trans[i][j] << ' ' << static_cast<char>(j + 'a') << '\n';
}

int main() {
    open(NAME);
    H();
    return 0;
}