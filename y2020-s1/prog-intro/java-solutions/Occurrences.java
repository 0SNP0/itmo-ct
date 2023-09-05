public class Occurrences implements Comparable<Occurrences> {

    private IntList line, column;

    public Occurrences() {
        line = new IntList();
        column = new IntList();
    }

    public int length() {
        return line.length();
    }

    public void add(int x, int y) {
        line.add(x);
        column.add(y);
    }

    public int[] get(int i) {
        if (i < length()) {
            return new int[] {line.get(i), column.get(i)};
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public int compareTo(Occurrences o) {
        if (this.length() == o.length()) {
            if (this.get(0)[0] == o.get(0)[0]) {
                return this.get(0)[1] - o.get(0)[1];
            } else {
                return this.get(0)[0] - o.get(0)[0];
            }
        } else {
            return this.length() - o.length();
        }
    }
}
