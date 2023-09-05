import java.util.Scanner;

public class J {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[][] arr = new int[n][n];
        for (int i = 0; i < n; i++) {
//            final char[] chars = sc.next().toCharArray();
            String line = sc.next();
            for (int j = 0; j < n; j++) {
                arr[i][j] = line.charAt(j) - '0';
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j > i && arr[i][j] > 0) {
                    for (int k = j + 1; k < n; k++) {
                        arr[i][k] = mod10(arr[i][k] - arr[j][k]);
                    }
                }
                System.out.print(arr[i][j]);
            }
            System.out.println();
        }
    }

    public static int mod10(int n) {
        return (n % 10 + 10) % 10;
    }
}
