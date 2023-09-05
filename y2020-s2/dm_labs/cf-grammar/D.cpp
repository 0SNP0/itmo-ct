#include <iostream>
#include <algorithm>
#include <vector>
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
using llong = long long;
#define Vect3(T) std::vector<std::vector<std::vector<T>>>
#define Vect3n(T, n) std::vector<std::vector<std::vector<T>>>(n, std::vector<std::vector<T>>(n, std::vector<T>(n)))

static const std::string NAME = "nfc";
static constexpr int SC = 'z' - 'a' + 1;
static constexpr int MOD = 1000000007;
static constexpr int CLOSE = -1;

class D {
    llong solve(std::string &&);
public:
    D();
private:
    int n;
    Vect3(llong) dp;
    std::vector<std::vector<std::pair<int, int>>> g;
    char start;
};

llong D::solve(std::string && str) {
    for (size_t i = 0; i < str.length(); ++i)
        for (int j = 0; j < SC; ++j)
            for (auto r : g.at(j))
                if (r.second == -1 && ((str.at(i) - 'a') == r.first)) dp.at(j).at(i).at(i) += 1;
    for (auto l = 2; l <= str.length(); ++l) 
        for (auto s = 0; s <= str.length() - l; ++s) {
            int e = s + l - 1;
            for (auto k = s; k < e; ++k)
                for (auto i = 0; i < SC; ++i)
                    for (auto r : g.at(i))
                        if (r.second != -1) dp.at(i).at(s).at(e) = (dp.at(i).at(s).at(e) + (dp.at(r.first).at(s).at(k) * dp.at(r.second).at(k + 1).at(e)) % MOD) % MOD;
        }
    return dp.at(start - 'A').at(0).at(str.length() - 1);
}
D::D() : n(next<int>()), dp(Vect3n(llong, 101)), g(101), start(next<char>()) {
    char from; std::string to;
    FOR(n) {
        from = next<char>() - 'A'; next<std::string>(); to = next<std::string>();
        if (to.length() == 1) g.at(from).emplace_back(to.at(0) - 'a', CLOSE);
        else g.at(from).emplace_back(to.at(0) - 'A', to.at(1) - 'A');
    }
    std::cout << solve(next<std::string>());
}
int main() {
    open_files(NAME);
    D();
    return 0;
}