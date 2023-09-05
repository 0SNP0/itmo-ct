package game;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
//        auto();
        manually();
    }

    public static void auto() {
        final int m = 5, n = 7;
        int k, pcount;
        Game game;
//        k = 4, pcount = 3;
//        game = new Game(new RandomPlayer(n, m), new SequentialPlayer(n, m), new SequentialPlayer(n, m));
        int result;
//        result = game.play(new MNKBoard(m, n, k, pcount));
//        System.out.println("Game result: " + result);

        final int rh = 5;
        k = 3;
        pcount = 4;
        game = new Game(new RandomPlayer(), new SequentialPlayer(), new RandomPlayer(), new HumanPlayer());
        result = game.play(new RhombusBoard(rh, k, pcount));
        System.out.println("Game result: " + result);
    }

    public static void manually() {
        try (Scanner in = new Scanner(System.in)) {
            System.out.println("2-4 - count of players\n0/1 - mnk/rhombus\n{m n}/n k\n1/2/0 - r/s/h *p");
            final int p = in.nextInt();
            final int t = in.nextInt();
            final int m = in.nextInt();
            final int n;
            if (t == 0) {
                n = in.nextInt();
            } else {
                n = m;
            }
            final int k = in.nextInt();
            Player[] players = new Player[p];
            for (int i = 0; i < p; i++) {
                int pt = in.nextInt();
                if (pt == 1) {
                    players[i] = new RandomPlayer();
                } else if (pt == 2) {
                    players[i] = new SequentialPlayer();
                } else {
                    players[i] = new HumanPlayer();
                }
            }
            final Game game = new Game(true, players);
            final Board board;
            if (t == 1) {
                board = new RhombusBoard(n, k, p);
            } else {
                board = new MNKBoard(m, n, k, p);
            }
            final int result = game.play(board);
            System.out.println("Game result: " + result);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
        }
    }
}
