#include <iostream>
#include <algorithm>
#include <vector>
void open_files(const std::string & name) {
    freopen((name + ".in").c_str(), "r", stdin);
    freopen((name + ".out").c_str(), "w", stdout);
}
template<typename T>
T next(std::istream & in = std::cin) { T res; in >> res; return std::move(res); }
std::string nextline(std::istream & in = std::cin) { std::string res; std::getline(in, res); return std::move(res); }

static const std::string NAME = "automaton";
static constexpr int C = 'z' - 'a' + 1;
static constexpr int CLOSE = -1;

class A {
    bool check(const std::string &, const int &, int) const;
public: 
    A();
    bool check(std::string &&) const;
private:
    const int cnt;
    std::vector<std::vector<std::pair<int, int>>> trans;
    const char start;
};

A::A() : cnt(next<int>()), trans(C, std::vector<std::pair<int, int>>()), start(next<char>() - 'A') {
    nextline();
    for (auto i = 0; i < cnt; ++i) {
        auto && fr = next<std::string>()[0] - 'A'; next<std::string>();
        auto && to = next<std::string>();
        trans[fr].emplace_back(to[0] - 'a', to.length() == 1 ? CLOSE : to[1] - 'A');
    }
    auto m = next<int>();
    for (nextline(); m; --m)
        std::cout << (check(nextline()) ? "yes\n" : "no\n");
}
int main() {
    open_files(NAME);
    A();
    return 0;
}

bool A::check(const std::string & word, const int & st, int pos) const {
    if (word.length() == pos) return st == CLOSE;
    else if (st == CLOSE) return false;
    auto cur = word[pos] - 'a';
    for (const auto & to : trans[st])
        if (cur == to.first && check(word, to.second, pos + 1))
            return true;
    return false;
}
bool A::check(std::string && word) const { return check(word, start, 0); }