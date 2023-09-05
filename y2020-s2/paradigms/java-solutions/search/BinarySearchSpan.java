package search;

public class BinarySearchSpan {
//    $ - args.length
//    PRE: args != null && args.length > 0, args[0..$] can be cast to int, in agrs[1..$] (as int) args[i] >= args[i+1] : 0 <= i < arr.length-1
    public static void main(String[] args) {
//        args.length > 0
        int x = Integer.parseInt(args[0]);
        int[] a = new int[args.length - 1];
        for (int i = 0; i < a.length; i++) {
            a[i] = Integer.parseInt(args[i + 1]);
        }
//        a[i] >= a[i+1] : 0 <= i < arr.length-1
//        int l = recSearchLeft(x, a, -1, a.lenght);
//        int r = recSearchRight(x, a, -1,  a.length);
        int l = iterSearchLeft(x, a);
        int r = iterSearchRight(x, a);
//        l <= r
        System.out.printf("%d %d", l, r - l);
    }

//    PRE: arr[i] >= arr[i+1] : 0 <= i < arr.length-1
    public static int iterSearchRight(int x, int[] arr) {
        int left = -1, right = arr.length;
//        INV: right - left >= 1 && (arr[right] < ans || right == arr.length)
        while (right - left > 1) {
//            right - left > 1
            int mid = (left + right) / 2;
//            INV && left < mid < right
            if (arr[mid] < x) {
//                INV && arr[mid] < x
                right = mid;
            } else {
//                INV && arr[mid] >= x
                left = mid;
            }
//            right - left < right' - left'
        }
//        0 <= right <= arr.length && right - left == 1
        return right;
    }
//    POST: 0 <= R <= arr.length && arr[R] < x && (R == 0 || arr[R-1] == x)

//    PRE: arr[i] >= arr[i+1] : 0 <= i < arr.length-1
    public static int iterSearchLeft(int x, int[] arr) {
        int left = -1, right = arr.length;
//        INV: right - left >= 1 && (arr[right] <= ans || right == arr.length)
        while (right - left > 1) {
//            right - left > 1
            int mid = (left + right) / 2;
//            INV && left < mid < right
            if (arr[mid] > x) {
//            INV && arr[mid] > x
                left = mid;
            } else {
//            INV && arr[mid] <= x
                right = mid;
            }
//            right - left < right' - left'
        }
//        0 <= right <= arr.length && right - left == 1
        return right;
    }
//    POST: 0 <= R <= arr.length && arr[R] == x && (R == 0 || arr[R-1] > x)

//    PRE: arr[i] <= arr[i+1] : 0 <= i < arr.length-1 && left >= -1 && right <= arr.length && (left == -1 || arr[left] >= x )
//    && (right == arr.length || x > arr[right])
    public static int recSearchRight(int x, int[] arr, int left, int right) {
        // :NOTE: нет пред/пост условия для условных операторов
        left = -10;
//        NAME: right - left >= 1 && (left == -1 || arr[left] >= x )
//          && (right == arr.length || x > arr[right])
        // :NOTE: неверное предусловие
//       Pre: NAME
        if (right - left == 1) {
//            NAME && right - left == 1 && (arr[right] < x || right == arr.lenght)
            return right;
        }
//        right - left > 1
//        NAME && left < mid < right
        int mid = (left + right) / 2;
//        PRE: -1 <= left < mid < right <= arr.length && arr[i] <= arr[i+1] : 0 <= i < arr.length-1
        return arr[mid] < x ? recSearchRight(x, arr, left, mid) : recSearchRight(x, arr, mid, right);
//        POST: right - left < right' - left'
    }
//    POST: right - left < right' - left'

//    PRE: arr[i] <= arr[i+1] : 0 <= i < arr.length-1 && left >= -1 && right <= arr.length && (left == -1 || right == arr.length || arr[left] > x >= arr[right])
    public static int recSearchLeft(int x, int[] arr, int left, int right) {
//        NAME: right - left >= 1 && (arr[right] <= ans || right == arr.length)
//        right - left == 1
        if (right - left == 1) {
//            NAME && right - left == 1 && (arr[right] == x && arr[left] > x || right == arr.lenght || left == -1)
            return right;
        }
//        right - left > 1
//        NAME && left < mid < right
        int mid = (left + right) / 2;
//        PRE: -1 <= left < mid < right <= arr.length && arr[i] <= arr[i+1] : 0 <= i < arr.length-1
        return arr[mid] > x ? recSearchLeft(x, arr, mid, right) : recSearchLeft(x, arr, left, mid);
//        POST: right - left < right' - left'
    }
}
//    POST: right - left < right' - left'
