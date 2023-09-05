import std.stdio;
import std.math;

bool satisfy(long a, long w, long h, long n) {
    return (a / w) * (a / h) >= n;
}

void main() {
    long n, w, h;
    readf(" %d %d %d", &w, &h, &n);
    if (w > h) {
        long temp = w;
        w = h;
        h = temp;
    }
    long r = (cast(long)sqrt(cast(double)n) + 1) * h;
    long l = 0;
    while (r - l > 1) {
        long m = l + (r - l) / 2;
        if (satisfy(m, w, h, n)) {
            r = m;
        } else {
            l = m;
        }
    }
    writeln(r);
}