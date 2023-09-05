import std.stdio;

long res = 0;

void merge(long[]arr, int l, int m, int r) {
    int n1, n2;
    n1 = m - l + 1;
    n2 = r - m;
    long[] larr = new long[n1 + 1];
    long[] rarr = new long[n2 + 1];
    for (int i = 0; i < n1; i++) {
        larr[i] = arr[l + i];
    }
    for (int j = 0; j < n2; j++) {
        rarr[j] = arr[m + j + 1];
    }
    larr[n1] = 2000000000;
    rarr[n2] = 2000000000;
    int i = 0, j = 0;
    for (int k = l; k <= r; k++) {
        if (larr[i] <= rarr[j]) {
            arr[k] = larr[i];
            i++;
        } else {
            arr[k] = rarr[j];
            j++;
            res += n1 - i;
        }
    }
    destroy(larr);
    destroy(rarr);
}

void mergeSort(long[] arr, int l, int r) {
    if (l < r) {
        int m = (l + r) / 2;
        mergeSort(arr, l, m);
        mergeSort(arr, m + 1, r);
        merge(arr, l, m, r);
    }
}

void main() {
    int n;
    readf( " %d", &n);
    long[] arr = new long[n];
    foreach (ref e; arr) {
        readf( " %d", e);
    }
    mergeSort(arr, 0, n - 1);
    writeln(res);
}