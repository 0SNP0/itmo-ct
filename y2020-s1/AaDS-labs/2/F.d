import std.stdio;

class Stack(T) {

    private {
        T[] arr;
        int size;
    }

    this() {
        arr = new T[1];
        size = 0;
    }

    private void resize() {
        if (size >= arr.length) {
            T[] buf = arr;
            arr = new T[](arr.length * 2);
            arr[0..size] = buf[];
        }
    }

    int length() {
        return size;
    }

    void push(T n) {
        resize();
        arr[size++] = n;
    }

    T pop() {
        return arr[--size];
    }

    T get() {
        return arr[size - 1];
    }

    bool isEmpty() {
        return size == 0;
    }
}

void main() {
    int n;
    int m = int.min;
    string r = "";
    auto s = new Stack!int();
    readf(" %d", &n);
    foreach (z; 0..n) {
        int i;
        readf(" %d", &i);
        while (!s.isEmpty() && i > s.get()) {
            int a = s.pop();
            r ~= "pop\n";
            if (a > m) {
                m = a;
            }
        }
        if (i < m) {
            write("impossible");
            return;
        }
        s.push(i);
        r ~= "push\n";
    }
    while (!s.isEmpty()) {
            s.pop();
            r ~= "pop\n";
    }
    write(r);
}
