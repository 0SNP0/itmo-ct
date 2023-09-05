import std.stdio;
import std.math;

real f(real x, real a, int vp, int vf) {
    return sqrt((1 - a) * (1 - a) + x * x) / vp + sqrt(a * a + (1 - x) * (1 - x)) / vf;
}

void main() {
    int vp, vf;
    real a;
    readf(" %d %d %f", &vp, &vf, &a);
    real l = 0, r = 1;
    while (r - l > 0.00001) {
        real m = (l * 2 + r) / 3;
        real n = (l + r * 2) / 3;
        if (f(m, a, vp, vf) < f(n, a, vp, vf)) {
            r = n;
        } else {
            l = m;
        }
    }
    writefln("%.5f", (l + r) / 2);
}