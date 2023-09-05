package game;

import java.util.Arrays;

public class RhombusBoard implements Board, Position {

    private final Cell[][] cells;
    private final int n, k, p;
    private final CellSet set;
    private int empty;
    private Cell turn;

    public RhombusBoard(int n, int k, int p) {
        if (k > n * 2 - 1 || n <= 0 || k <= 0) {
            throw new AssertionError("Wrong set {n, k}");
        }
        this.n = n;
        this.k = k;
        this.p = p;
        this.cells = new Cell[n * 2 - 1][n * 2 - 1];
        this.set = new CellSet(p);
        this.empty = n * (n - 1) * 2 + 1;
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
    }

    @Override
    public Position getPosition() {
        return new PositionForPlayer(this);
    }

    @Override
    public Cell getCell() {
        return turn;
    }

    @Override
    public Result makeMove(Move move) {
        if (!isValid(move)) {
            return Result.LOSE;
        }

        cells[move.getRow() + n - 1][move.getColumn() + n - 1] = move.getValue();
        empty--;

        final int[][] dir = new int[4][0];
        dir[0] = new int[] {1, 0};
        dir[1] = new int[] {0, 1};
        dir[2] = new int[] {1, 1};
        dir[3] = new int[] {1, -1};
        for (int[] e : dir) {
            int sum = 1;
            for (int i = 2; i > 0; i--) {
                for (int r = move.getRow() + e[0], c = move.getColumn() + e[1]; abs(c) + abs(r) < n && cells[r + n - 1][c + n - 1] == turn; r += e[0], c += e[1]) {
                    if (++sum >= k) {
                        return Result.WIN;
                    }
                }
                e[0] *= -1;
                e[1] *= -1;
            }
        }

        if (empty == 0) {
            return Result.DRAW;
        }

        turn = set.next(turn);
        return Result.UNKNOWN;
    }

    @Override
    public boolean isValid(Move move) {
        return abs(move.getRow()) + abs(move.getColumn()) < n
                && cells[move.getRow() + n - 1][move.getColumn() + n - 1] == Cell.E
                && turn == move.getValue();
    }

    @Override
    public Cell getCell(int r, int c) {
        return cells[r + n - 1][c + n - 1];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int r = 1 - n; r < n; r++) {
            sb.append("\n");
            if (r >= 0) {
                sb.append(' ');
            }
            sb.append(r);
            for (int i = abs(r); i > 0; i--) {
                sb.append(' ');
            }
            for (int c = 1 - n; c < n; c++) {
                if (abs(r) + abs(c) < n) {
                    sb.append(set.get(cells[r + n - 1][c + n - 1]));
                }
            }
        }
        return sb.toString();
    }

    @Override
    public int rows() {
        return 2 * n - 1;
    }

    @Override
    public int columns() {
        return 2 * n - 1;
    }

    @Override
    public int isRhombus() {
        return n;
    }

    public int abs(int num) {
        if (num < 0) {
            return -num;
        }
        return num;
    }

    @Override
    public int countOfPlayers() {
        return p;
    }
}
