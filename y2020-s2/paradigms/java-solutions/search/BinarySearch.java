package search;

public class BinarySearch {

//    Pre: arr is sorted in non-increasing
//    Post: the minimum value of i : a[i] <= x
//    0 <= i <= arr.length
    public static int iterSearch(int x, int[] arr) {
        int left = -1, right = arr.length;
        while (right - left > 1) {
            int mid = (left + right) / 2;
            if (arr[mid] > x) {
                left = mid;
            } else {
                right = mid;
            }
        }
        return right;
    }

//    Pre: arr is sorted in non-increasing, left >= -1, right <= arr.lenght
//    Post: the minimum value of i : a[i] <= x
//    left < i <= right
    public static int recSearch(int x, int[] arr, int left, int right) {
        if (right - left == 1) {
            return right;
        }
        int mid = (left + right) / 2;
//        left < mid < right
        return arr[mid] > x ? recSearch(x, arr, mid, right) : recSearch(x, arr, left, mid);
    }

//    Pre: arr is sorted in non-increasing
//    Post: the minimum value of i : a[i] <= x
//    0 <= i <= arr.length
    public static int recSearch(int x, int[] arr) {
        return recSearch(x, arr, -1, arr.length);
    }

//    Pre: args.length > 0, args[1..$] (as int) is sorted in non-increasing
    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int[] a = new int[args.length - 1];
        for (int i = 0; i < a.length; i++) {
            a[i] = Integer.parseInt(args[i + 1]);
        }
        System.out.print(recSearch(x, a));
//        Post: Post: the minimum value of i : (int) args[i+1] <= x, 0 <= i < args.length
    }
}
