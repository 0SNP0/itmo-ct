package game;

import java.util.Random;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class RandomPlayer implements Player {

    private final Random random;

    public RandomPlayer(final Random random) {
        this.random = random;
    }

    public RandomPlayer() {
        this(new Random());
    }

    @Override
    public Move move(final Position position, final Cell cell) {
        int n = position.rows();
        int m = position.columns();
        int rh = position.isRhombus();
        while (true) {
            int r = random.nextInt(n);
            int c = random.nextInt(m);
            if (rh > 0) {
                r -= (rh - 1);
                c -= (rh - 1);
            }
            final Move move = new Move(r, c, cell);
            if (position.isValid(move)) {
                return move;
            }
        }
    }
}
