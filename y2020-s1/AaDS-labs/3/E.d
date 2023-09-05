import std.stdio;
private:

int min(int a, int b, int c) {
    return a < c ? (a < b ? a : b) : (b < c ? b : c);
}

int levenstain(string str1, string str2) {
    int[] di1 = new int[str2.length + 1];
    int[] di = new int[str2.length + 1];
    for (auto j = 0; j <= str2.length; j++) {
        di[j] = j;
    }
    for (auto i = 1; i <= str1.length; i++) {
        di1[] = di[];
        di[0] = i;
        for (auto j = 1; j <= str2.length; j++) {
            int cost = (str1[i - 1] != str2[j - 1]) ? 1 : 0;
            di[j] = min(di1[j] + 1, di[j - 1] + 1, di1[j - 1] + cost);
        }
    }
    return di[di.length - 1];
}

void main() {
    string s1 = readln();
    string s2 = readln();
    write(levenstain(s1, s2));
}