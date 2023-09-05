package game;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class SequentialPlayer implements Player {

    @Override
    public Move move(final Position position, final Cell cell) {
        int n = position.rows();
        int m = position.columns();
        int rh = position.isRhombus();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                final Move move;
                if (rh > 0) {
                    move = new Move(r - rh + 1, c - rh + 1, cell);
                } else {
                    move = new Move(r, c, cell);
                }
                if (position.isValid(move)) {
                    return move;
                }
            }
        }
        throw new IllegalStateException("No valid moves");
    }
}
