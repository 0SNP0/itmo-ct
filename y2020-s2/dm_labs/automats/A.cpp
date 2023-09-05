#include <fstream>
#include <vector>
#include <unordered_map>

const std::string NAME = "problem1";

class State 
{
public:
    void add(const char & s, const int & t)
    {
        transit.insert({s, t});
    }
    int get(const char & s) const 
    {
        return transit.count(s) ? transit.at(s) : -1;
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
    std::unordered_map<char, int> transit;
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
    int cur = 0;
    for (const auto & s : word) {
        cur = stats[cur].get(s);
        if (cur < 0) {
            fout << "Rejects";
            return 0;
        }
    }
    fout << (stats[cur].isAdmissible() ? "Accepts" : "Rejects");

    return 0;
}