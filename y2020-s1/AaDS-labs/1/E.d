import std.stdio;
import std.random;

void quickSort(int[] arr, int l, int r) {
    if (l < r) {
        int i = l, j = r;
        int pivot = arr[l + Random(unpredictableSeed).front % (r - l + 1)];
        while (i <= j) {
            for (; arr[i] < pivot; i++){}
            for (; arr[j] > pivot; j--){}
            if (i <= j) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }
        }
        quickSort(arr, l, j);
        quickSort(arr, i, r);
    }
}

int binSearchLeft(int[] arr, int x) {
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

int binSearchRight(int[] arr, int x) {
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
    int n, k, l, r;
    readf(" %d", &n);
    int[] arr = new int[n];
    foreach (ref e; arr) {
        readf(" %d", e);
    }
    quickSort(arr, 0, n - 1);
    readf(" %d", &k);
    for (; k; k--) {
        readf(" %d %d", l, r);
        writef("%d ", binSearchRight(arr, r) - binSearchLeft(arr, l) + 1);
    }
}