import std.stdio;
import std.math;

real f(real x, real c) {
    return x * x + sqrt(x) - c;
}

void main() {
    real c;
    readf(" %f", &c);
    real r = sqrt(c);
    real l = 0;
    while (r - l > 0.0000009) {
        real m = (r + l) / 2;
        if (f(m, c) < 0) {
            l = m;
        } else {
            r = m;
        }
    }
    writefln("%.6f", r);
}