import std.stdio;

class HeapIntMax {
    int[] arr;
    int n;
    this(int size) {
        this.arr = new int[size];
        this.n = 0;
    }
    void siftDown(int i) {
        while (true) {
            int j = i;
            if (2 * i + 1 < n && arr[2 * i + 1] > arr[j]) {
                j = 2 * i + 1;
            }
            if (2 * i + 2 < n && arr[2 * i + 2] > arr[j]) {
                j = 2 * i + 2;
            }
            if (i == j) {
                break;
            }
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            i = j;
        }
    }
    void siftUp(int i) {
        while (i > 0 && arr[i] > arr[(i - 1) / 2]) {
            int temp = arr[i];
            arr[i] = arr[(i - 1) / 2];
            arr[(i - 1) / 2] = temp;
            i = (i - 1) / 2;
        }
    }
    int removeMax() {
        int res = arr[0];
        arr[0] = arr[--n];
        siftDown(0);
        return res;
    }
    void insert(int x) {
        arr[n++] = x;
        siftUp(n - 1);
    }
}

void main() {
    int n, x;
    readf(" %d", &n);
    HeapIntMax heap = new HeapIntMax(n);
    for (; n; n--) {
        readf(" %d", &x);
        if (!x) {
            readf( " %d", &x);
            heap.insert(x);
        } else {
            writeln(heap.removeMax());
        }
    }
}