import std.stdio;
import std.random;

int binSearchRight(int[] arr, int x) {
    int l = -1, r = arr.length;
    while (r - l > 1) {
        int m = l + (r - l) / 2;
        if (arr[m] < x) {
            l = m;
        } else {
            r = m;
        }
    }
    return r;
}

int binSearchLeft(int[] arr, int x) {
    int l = -1, r = arr.length;
    while (r - l > 1) {
        int m = l + (r - l) / 2;
        if (arr[m] > x) {
            r = m;
        } else {
            l = m;
        }
    }
    return l;
}

void main() {
    int n, k, x;
    readf(" %d", &n);
    readf(" %d", &k);
    int[] arr = new int[n];
    foreach (ref e; arr) {
        readf(" %d", e);
    }
    for (; k; k--) {
        readf(" %d", x);
        int l = binSearchLeft(arr, x);
        if (l == -1) {
            l++;
        }
        int r = binSearchRight(arr, x);
        if (r == n) {
            r--;
        }
        if (arr[r] - x < x - arr[l]) {
            x = arr[r];
        } else {
            x = arr[l];
        }
        writeln(x);
    }
}