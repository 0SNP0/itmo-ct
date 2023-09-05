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

static const std::string NAME = "cf";
static constexpr int UC = 'z' + 1;
static constexpr int CLOSE = -1;

class E {};

int main() {
    open_files(NAME);
    E();
    return 0;
}