import std.stdio;
import std.format;


class DSU {

    private {
        // int[][] list;
        int[] root, min, max, count;
        int size;
    }

    this(int n) {
        size = n;
        root = new int[n];
        min = new int[n];
        max = new int[n];
        count = new int[n];
        for (auto i = 0; i < n; i++) {
            root[i] = i;
            min[i] = i;
            max[i] = i;
            count[i] = 1;
        }
        // list = new int[][n];
        // foreach (i, ref e; count) {
        //     e ~= [i];
        // }
    }

    int getmin(int n) {
        int e = n;
        while (min[n] != n) {
            n = min[n];
        }
        min[e] = n;
        return n;
    }

    int getmax(int n) {
        int e = n;
        while (max[n] != n) {
            n = max[n];
        }
        max[e] = n;
        return n;
    }

    int getroot(int n) {
        int e = n;
        while (root[n] != n) {
            n = root[n];
        }
        root[e] = n;
        return n;
    }

    int getcount(int n) {
        return count[getroot(n)];
    }

    void dunion(int a, int b) {
        if (getroot(a) == getroot(b)) return;
        // list[a] ~= list[b];
        // list[b] = list[a];
        if (getmin(a) < getmin(b)) {
            min[getmin(b)] = getmin(a);
        } else {
            min[getmin(a)] = getmin(b);
        }
        if (getmax(a) > getmax(b)) {
            max[getmax(b)] = getmax(a);
        } else {
            max[getmax(a)] = getmax(b);
        }
        count[getroot(a)] += getcount(b);
        root[getroot(b)] = getroot(a);
    }
    
    string get(int n) {
        return format!"%d %d %d\n"(getmin(n) + 1, getmax(n) + 1, getcount(n));
    }
}

void main() {
    int n;
    readf(" %d\n", &n);
    auto dsu = new DSU(n);
    string act;
    while (readf("%s ", &act)) {
        if (act == "union") {
            int a, b;
            readf("%d %d\n", &a, &b);
            dsu.dunion(a - 1, b - 1);
        } else {
            int a;
            readf("%d\n", &a);
            writeln(dsu.get(a - 1));
        }
    }
}