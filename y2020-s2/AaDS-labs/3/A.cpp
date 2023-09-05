#include <iostream>
#include <vector>

template <typename T>
T next(std::istream & strm = std::cin) { T r; strm >> r; return std::move(r); }
typedef std::vector<int> vect_int;

int main() {
    const size_t n = next<int>();
    size_t max_p = 0;
    for (size_t i = 1; i < n; i *= 2) ++max_p;
    std::vector<vect_int> tree(n + 1, vect_int(max_p));
    for (size_t i = 1; i < tree.size(); ++i)
        std::cin >> tree[i][0];
    for (size_t p = 1; p < max_p; ++p)
        for (size_t i = 0; i < tree.size(); ++i)
            tree[i][p] = tree[tree[i][p - 1]][p - 1];
    for (size_t i = 1; i < tree.size(); ++i) {
        std::cout << i << ": ";
        for (const auto & e : tree[i]) {
            if (e == 0) break;
            std::cout << e << ' ';
        }
        std::cout << std::endl;
    }
    return 0;
}