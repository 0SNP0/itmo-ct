import std.stdio;

void main() {
    int n, m;
    readf(" %d %d", &n, &m);
    int[][] c = new int[][](n, m);
    for (auto i = 0; i < n; ++i) for (auto j = 0; j < m; ++j) {
        readf(" %d", &c[i][j]);
    }
    for (auto i = 1; i < n; ++i) {
        c[i][0] += c[i - 1][0];
    }
    for (auto i = 0; i < n; ++i) for (auto j = 1; j < m; ++j) {
        if (i > 0 && c[i - 1][j] > c[i][j - 1]) {
            c[i][j] += c[i - 1][j];
        } else {
            c[i][j] += c[i][j - 1];
        }
    }
    writeln(c[$ - 1][$ - 1]);
    string s = "";
    for (--n, --m; n + m > 0;) {
        if (m == 0 || (n > 0 && m > 0 && c[n - 1][m] > c[n][m - 1])) {
            s = "D" ~ s;
            --n;
        } else {
            s = "R" ~ s;
            --m;
        }
    }
    write(s);
}