#include <fstream>
#include <vector>
#include <unordered_map>
#include <unordered_set>

const std::string NAME = "problem2";

using Set = std::unordered_set<int>;

class State 
{
public:
    void add(const char & s, const int & t)
    {
        if (transit.count(s)) transit[s].insert(t);
        else transit.insert({s, Set{t}});
    }
    std::unordered_set<int> get(const char & s) const 
    {
        return transit.count(s) ? transit.at(s) : std::unordered_set<int>{};
    }
    bool isAdmissible() const
    {
        return admit;
    }
    void makeAdmissible()
    {
        admit = true;
    }

private:
    std::unordered_map<char, Set> transit;
    bool admit = false;
};

int main() 
{
    std::ifstream fin(NAME + ".in");
    std::ofstream fout(NAME + ".out");

    std::string word;
    int n, m, k;
    fin >> word >> n >> m >> k;
    std::vector<State> stats(n);
    for (int a; k; --k) {
        fin >> a;
        stats[a - 1].makeAdmissible();
    }
    for (char s; m; --m) {
        int a, b;
        fin >> a >> b >> s;
        stats[a - 1].add(s, b - 1);
    }
    Set cur{0};
    for (const auto & s : word) {
        Set buf(cur);
        cur.clear();
        for (const auto & e : buf) {
            Set res = stats[e].get(s);
            cur.insert(res.begin(), res.end());
        }
        if (cur.empty()) break;
    }
    for (const auto & e : cur) {
        if (stats[e].isAdmissible()) {
            fout << "Accepts";
            return 0;
        }
    }
    fout << "Rejects";
    return 0;
}