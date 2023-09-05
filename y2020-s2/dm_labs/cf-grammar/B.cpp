#include <iostream>
#include <algorithm>
#include <vector>
void open_files(const std::string & name) {
    freopen((name + ".in").c_str(), "r", stdin);
    freopen((name + ".out").c_str(), "w", stdout);
}
template <typename T>
T next(std::istream & in = std::cin) { T res; in >> res; return std::move(res); }
// std::string nextline(std::istream & in = std::cin) { std::string res; std::getline(in, res); return std::move(res); }
template <typename T>
void sort(T & t) { std::sort(t.begin(), t.end()); }

static const std::string NAME = "epsilon";

class B {
    void solve();
public:
    B();
private:
    int n;
    std::vector<bool> eps, used;
    std::vector<std::pair<std::string, std::string>> rule;
    std::vector<std::string> ans;
};

B::B() : n(next<int>()), eps(1000), used(n) {
    auto start = next<char>();
    for (auto i = 0; i < n; ++i) {
        static std::string a, b;
        a = next<std::string>();
        if (a == "->") {
            b = next<std::string>();
            a = rule.back().second;
            rule.back().second = "";
        } else {
            next<std::string>();
            b = next<std::string>();
        }
        rule.emplace_back(a, b);
    }
    solve();
    for (const auto & e : ans)
        std::cout << e << ' ';
}
int main() {
    open_files(NAME);
    B();
    return 0;
}

void B::solve() {
    for (bool now, added = true; added; added = now) {
        now = false;
        for (auto i = 0; i < n; ++i) {
            if (used.at(i)) continue;
            const std::pair<std::string, std::string> & r = rule.at(i);
            auto is_new = true;
            for (auto & c : r.second) {
                if (!eps.at(c)) {
                    is_new = false;
                    break;
                }
            }
            if (is_new) {
                ans.push_back(r.first);
                eps.at(r.first[0]) = true;
                used.at(i) = true;
                now = true;
            }
        }
    }
    sort(ans);
}