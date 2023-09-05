import java.util.Scanner;

public class F {

    public static class Node {
        long val;
        boolean set;
        long add;
        int left;
        int right;

        public Node(long value, boolean set, long add, int left, int right) {
            this.val = value;
            this.set = set;
            this.add = add;
            this.left = left;
            this.right = right;
        }
    }

    public static int k;
    public static Node[] tree;
    public static long min;

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        int n = in.nextInt(), m = in.nextInt();
        for (k = 1; k < n; k *= 2);
        tree = new Node[2 * k - 1];
        for (int i = k - 1; i < k - 1 + n; i++) {
            tree[i] = new Node(0, false, 0, i, i);
        }
        for (int i = k - 1 + n; i < 2 * k - 1; i++) {
            tree[i] = new Node(Long.MAX_VALUE, false, 0, i, i);
        }
        for (int i = k - 2; i >= 0; --i) {
            tree[i] = new Node(Long.min(tree[i * 2 + 1].val, tree[i * 2 + 2].val),
                    false, 0, tree[i * 2 + 1].left, tree[i * 2 + 2].right);
        }
        for (; m > 0; m--) {
            int a = in.nextInt();
            int l = in.nextInt() + k - 2, r = in.nextInt() + k - 2;
            switch (a) {
                case 1:
                    long val = in.nextLong();
                    add(0, val, l, r - 1);
                    break;
                case 2:
                    System.out.println(getMin(l, r - 1));
                    break;
            }
        }
    }

    public static long getMin(int l, int r) {
        min = Long.MAX_VALUE;
        min(0, l, r);
        return min;
    }

    public static void add(int i, long val, int l, int r) {
        if (2 * i + 2 >= 2 * k - 1) {
            if (tree[i].left >= l && tree[i].right <= r) {
                tree[i].add += val;
            }
            return;
        }

        if (tree[i].right < l || tree[i].left > r) {
            return;
        }

        if (tree[i].left >= l && tree[i].right <= r) {
            tree[i].add += val;
            return;
        }

        push(i);

        add(i * 2 + 1, val, l, r);
        add(i * 2 + 2, val, l, r);

        tree[i].val = Long.min(
                tree[2 * i + 1].val + tree[2 * i + 1].add,
                tree[2 * i + 2].val + tree[2 * i + 2].add
        );
    }
    
    public static void min(int i, int l, int r) {
        if (2 * i + 2 >= 2 * k - 1) {
            if (tree[i].left >= l && tree[i].right <= r) {
                min = Long.min(min, tree[i].val + tree[i].add);
            }
            return;
        }
        if (tree[i].right < l || tree[i].left > r) {
            return;
        }
        if (tree[i].left >= l && tree[i].right <= r) {
            min = Long.min(min, tree[i].val + tree[i].add);
            return;
        }
        push(i);
        min(i * 2 + 1, l, r);
        min(i * 2 + 2, l, r);
        tree[i].val = Long.min(
                tree[2 * i + 1].val + tree[2 * i + 1].add,
                tree[2 * i + 2].val + tree[2 * i + 2].add
        );
    }

    public static void push(int i) {
        if (tree[i].set) {
            tree[2 * i + 1].val = tree[i].val;
            tree[2 * i + 2].val = tree[i].val;
            tree[i].set = false;
            tree[2 * i + 1].set = true;
            tree[2 * i + 2].set = true;
            tree[2 * i + 1].add = 0;
            tree[2 * i + 2].add = 0;
        }
        if (tree[i].add != 0) {
            tree[2 * i + 1].add += tree[i].add;
            tree[2 * i + 2].add += tree[i].add;
            tree[i].add = 0;
        }
    }
}
