import std.stdio;

class Staque {

    private {
        int[] arr;
        int l;
        int r;
        int[int] n;
    }

    this() {
        arr = new int[1];
        l = 0;
        r = 0;
    }

    private void resize() {
        if (r >= arr.length) {
            int[] buf = arr;
            arr = new int[](arr.length * 2);
            arr[0..r] = buf[];
        }
    }

    int length() {
        return r - l;
    }
    
    void pushBack(int id) {
        resize();
        n[id] = r;
        arr[r++] = id;
    }

    int popFront() {
        return arr[l++];
    }

    int popBack() {
        return arr[--r];
    }

    int findFront(int id) {
        return n[id] - l;
    }

    int getFront() {
        return arr[l];
    }
}

void main() {
    int n, count = 0;
    readf(" %d", &n);
    Staque q = new Staque();
    foreach (z; 0..n) {
        int a;
        readf(" %d", &a);
        final switch(a) {
        case 1:
            readf(" %d", &a);
            q.pushBack(a);
            break;
        case 2:
            q.popFront();
            break;
        case 3:
            q.popBack();
            break;
        case 4:
            readf(" %d", &a);
            writeln(q.findFront(a));
            break;
        case 5:
            writeln(q.getFront());
            break;
        }
    }
}