import std.stdio;

class StackAndMin {

    private {
        int[] arr;
        int[] min;
        int size;
    }

    this() {
        arr = new int[1];
        min = new int[1];
        size = 0;
    }

    int length() {
        return size;
    }

    private void resize() {
        if (size >= arr.length) {
            int[] buf = arr;
            arr = new int[arr.length * 2];
            arr[0..size] = buf[];
            buf = min;
            min = new int[arr.length];
            min[0..size] = buf[];
        }
    }

    void add(int n) {
        resize();
        arr[size] = n;
        if (size == 0 || n < min[size - 1]) {
            min[size] = n;
        } else {
            min[size] = min[size - 1];
        }
        size++;
    }

    int pop() {
        return arr[--size];
    }

    int minis() {
        return min[size - 1];
    }
}

void main() {
    int n;
    readf(" %d", &n);
    StackAndMin s = new StackAndMin();
    foreach (int k; 0..n) {
        int a;
        readf(" %d", &a);
        final switch(a) {
        case 1:
            int b;
            readf(" %d", &b);
            s.add(b);
            break;
        case 2:
            s.pop();
            break;
        case 3:
            writeln(s.minis());
            break;
        }
    }
}