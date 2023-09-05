import std.stdio;
import std.conv;

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
}

void main() {
    auto s = new Stack!int();
    string t;
    while (true) {
        readf(" %s ", &t);
        if (t[0] == '+' || t[0] == '-' || t[0] == '*') {
            if (s.length() == 1) {
                writeln(s.pop());
                return;
            }
            auto b = s.pop();
            auto a = s.pop();
            final switch(t[0]) {
            case '+':
                s.push(a + b);
                break;
            case '-':
                s.push(a - b);
                break;
            case '*':
                s.push(a * b);
                break;
            }
        } else {
            s.push(to!int(t));
        }
    }
}