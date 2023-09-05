import std.stdio;

class Pair {

    public {
        int k;
        int v;
    }

    this(int k, int v) {
        this.k = k;
        this.v = v;
    }
}

class PStack {

    private {
        Pair[] arr;
        int size;
    }

    this() {
        arr = new Pair[](1);
        size = 0;
    }

    int length() {
        return size;
    }

    private void resize() {
        if (size >= arr.length) {
            Pair[] buf = arr;
            arr = new Pair[](arr.length * 2);
            arr[0..size] = buf[];
        }
    }

    void add(int[2] n) {
        resize();
        arr[size++] = new Pair(n[0], n[1]);
    }

    Pair back() {
        return arr[size - 1];
    }

    Pair pop() {
        return arr[--size];
    }

    void popInc() {
        arr[size - 1].v++;
    }

    bool isEmpty() {
        return size == 0;
    }
}

void main() {
    int n, count = 0;
    readf(" %d", &n);
    PStack s = new PStack();
    foreach (z; 0..n) {
        int k;
        readf(" %d", &k);
        if (s.isEmpty()) {
            s.add([k, 1]);
        } else if (s.back().k == k) {
            s.popInc();
        } else {
            if (s.back().v >= 3) {
                count += s.pop().v;
            }
            if (s.back().k == k) {
                s.popInc();
            } else {
                s.add([k, 1]);
            }
        }
    }
    if (s.back().v >= 3) {
        count += s.pop().v;
    }
    writeln(count);
}