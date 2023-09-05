import std.stdio;

bool satisfy(long[] arr, int k, long m) {
    long sum = 0;
    foreach (e; arr) {
        if (sum + e > m) {
            k--;
            if (e > m) {
                return false;
            }
            sum = e;
        } else {
            sum += e;
        }
    }
    return k > 0;
}

void main() {
    int n, k;
    readf(" %d %d", &n, &k);
    long[] arr = new long[n];
    long l = 0, r = 0;
    foreach (ref e; arr) {
        readf(" %d", e);
        r += e;
    }
    while (r - l > 1) {
        long m = l + (r - l) / 2;
        if (satisfy(arr, k, m)) {
            r = m;
        } else {
            l = m;
        }
    }
    writeln(r);
}