import java.io.IOException;
import java.util.Arrays;

public class ReverseHexDec {

    public static void main(String[] args) {
        int[][] nums = new int[10][];
        int l = 0;
        try (Scanner sc = new Scanner(System.in)) {
            for (int sep = sc.countOfLineSep(); sc.isNotEnd() || sep > 0;) {
                for (; sep > 0; sep--) {
                    if (l == nums.length) {
                        nums = Arrays.copyOf(nums, nums.length * 2);
                    }
                    nums[l++] = new int[0];
                }
                if (l == nums.length) {
                    nums = Arrays.copyOf(nums, nums.length * 2);
                }
                int[] a = new int[10];
                int n = 0;
                while (sc.isNotEnd()) {
                    if (n == a.length) {
                        a = Arrays.copyOf(a, a.length * 2);
                    }
                    a[n++] = sc.nextInt();
                    sep = sc.countOfLineSep();
//                    System.err.println(sep);
                    if (sep > 0) {
                        nums[l++] = Arrays.copyOf(a, n);
                        sep--;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("IO exception: " + e.getMessage());
        }

//        System.err.println();
        for (l--; l >= 0; l--) {
            for (int i = nums[l].length - 1; i >= 0; i--) {
                System.out.printf("%d ", nums[l][i]);
//                System.err.printf("%d ", nums[l][i]);
            }
            System.out.println();
//            System.err.println();
        }
    }
}