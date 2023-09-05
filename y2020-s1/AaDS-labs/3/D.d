import std.stdio;

const MOD = 1_000_000_000;

int[][] next = [[4, 6], [6, 8], [7, 9], [4, 8], [0, 3, 9], [], [0, 1, 7], [2, 6], [1, 3], [2, 4]];

void main() {
    int n;
    readf(" %d", &n);
    int[][] d = new int[][](n, 10);
    foreach (ref e; d[0]) {
        e = 1;
    }
    for (auto i = 1; i < n; ++i) {
        for (auto j = 0; j < 10; ++j) {
            foreach (ref e; next[j]) {
                d[i][j] = (d[i][j] + d[i - 1][e]) % MOD;
            }
        }
    }
    if (n == 1) {
        write(8);
    } else {
        int sum = d[n - 1][9];
        for (auto i = 1; i < 8; ++i) {
            sum = (sum + d[n - 1][i]) % MOD;
        }
        write(sum);
    }
}