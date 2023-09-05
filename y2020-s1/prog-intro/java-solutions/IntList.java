import java.util.Arrays;

public class IntList {
    private int size;
    private int[] arr;

    public IntList() {
        size = 0;
        arr = new int[1];
    }

    public int length() {
        return size;
    }

    private void resize() {
        if (size >= arr.length) {
            arr = Arrays.copyOf(arr, arr.length * 2);
        }
    }

    public void add(int n) {
        resize();
        arr[size++] = n;
    }

    public int get(int i) {
        if (i < size) {
            return arr[i];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

//    public int pop() {
//        return arr[--size];
//    }

}
