import std.stdio;

class Queue {
    private {
        int[] arr;
        int l;
        int r;
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
    
    void pushBack(int n) {
        resize();
        arr[r++] = n;
    }

    int popFront() {
        return arr[l++];
    }
}

class Stack {
    private {
        int[] arr;
        int r;
    }

    this() {
        arr = new int[1];
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
        return r;
    }
    
    void push(int n) {
        resize();
        arr[r++] = n;
    }

    int pop() {
        return arr[--r];
    }
}

class DQueue {

    private {
        Queue q;
        Stack s;
    }

    this() {
        q = new Queue();
        s = new Stack();
    }

    int length() {
        return q.length() + s.length();
    }

    void pushBack(int n) {
        q.pushBack(n);
    }

    void pushFront(int n) {
        s.push(n);
    }

    int popFront() {
        if (s.length()) {
            return s.pop();
        }
        return q.popFront();
    }
}

class QwM {

    private {
        Queue q1;
        DQueue q2;
    }

    this() {
        q1 = new Queue();
        q2 = new DQueue();
    }

    void add(int n) {
        q2.pushBack(n);
        if (q2.length() > q1.length()) {
            q1.pushBack(q2.popFront());
        }
    }

    void addp(int n) {
        if (q1.length() > q2.length()) {
            q2.pushFront(n);
        } else {
            q1.pushBack(n);
        }
    }

    int pop() {
        if (q1.length() <= q2.length()) {
            q1.pushBack(q2.popFront());
        }
        return q1.popFront();
    }
}

void main() {
    int n;
    readf(" %d", &n);
    QwM queue = new QwM();
    foreach (z; 0..n) {
        char a;
        readf(" %c", &a);
        if (a == '-') {
            writeln(queue.pop());
        } else {
            int i;
            readf(" %d", &i);
            if (a == '+') {
                queue.add(i);
            } else {
                queue.addp(i);
            }
        }
    }
}