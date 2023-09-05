import std.stdio;

void main() {
    int[101] count;
    int n;
    readf(" %d", &n);
    for (; n; n--) {
        int e;
        readf(" %d", e);
        count[e]++;
    }
    for (int i = 0; i <= 100; i++) {
        for (; count[i]; count[i]--) {
            writef("%d ", i);
        }
    }
}