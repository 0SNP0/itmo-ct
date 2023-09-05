import std.stdio;
import std.conv;


void main() {
    int n, k;
    readf(" %d %d", &n, &k);
    int[] c = new int[n];
    int[] s = new int[n];
    int[] p = new int[n];
    for (auto i = 1; i < n - 1; ++i) {
        readf(" %d", &c[i]);
    }
    for (auto i = 1; i < n; ++i) {
        p[i] = i - 1;
        s[i] = s[p[i]];
        for (auto j = 2; j <= i && j <= k; ++j) {
            if (s[i - j] > s[i]) {
                p[i] = i - j;
                s[i] = s[p[i]];
            }
        }
        s[i] += c[i];
    }
    writeln(s[$ - 1]);
    string w = to!string(n);
    int cnt = 0;
    for (auto i = n - 1; i != 0; i = p[i]) {
        ++cnt;
        w = to!string(p[i] + 1) ~ " " ~ w;
    }
    writeln(cnt);
    write(w);
}