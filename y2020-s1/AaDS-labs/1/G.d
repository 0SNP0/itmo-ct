import std.stdio;

int gcf(int a, int b) {
    if (a * b == 0) {
        return a + b;
    }
    return gcf(a < b ? a : b, a < b ? b % a : a % b);
}

void main() {
    long n, time;
    int x, y;
    readf(" %d %d %d", &n, &x, &y);
    if (x > y) {
        int temp = x;
        x = y;
        y = temp;
    }
    time += x;
    n--;
    int fcm = x * y / gcf(x, y);
    int cfc = fcm / x + fcm / y;
    time += fcm * (n / cfc);
    n %= cfc;
    for (int a = 0, b = 0; n > 0; time++) {
        a = (a + 1) % x;
        b = (b + 1) % y;
        if (a == 0) n--;
        if (b == 0) n--;
    }
    writeln(time);
}