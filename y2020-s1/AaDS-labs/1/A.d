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

void main() {
    int n;
    readf(" %d", &n);
    int[] arr = new int[n];
    foreach (ref e; arr) {
        readf(" %d", e);
    }
    quickSort(arr, 0, n - 1);
    foreach (e; arr) {
        writef("%d ", e);
    }
}