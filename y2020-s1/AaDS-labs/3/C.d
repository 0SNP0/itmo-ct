import std.stdio;
import std.conv;


void main() {
    int n;
    readf(" %d", &n);
    int[] arr = new int[n];
    foreach (ref e; arr) {
        readf(" %d", &e);
    }
    int[] prev = new int[n];
    int[] cnt = new int[n + 1];
    int m = n;
    for (auto i = 0; i < n; ++i) {
        int p = n;
        for (auto j = i - 1; j >= 0; --j) {
            if (arr[j] < arr[i] && cnt[j] > cnt[p]) {
                p = j;
            }
        }
        prev[i] = p;
        cnt[i] = cnt[p] + 1;
        if (cnt[i] > cnt[m]) {
            m = i;
        }
    }
    writeln(cnt[m]);
    string s = to!string(arr[m]);
    for (m = prev[m]; cnt[m] > 0; m = prev[m]) {
        s = to!string(arr[m]) ~ " " ~ s;
    }
    write(s);
}