package game;

import java.util.Arrays;

public class MNKBoard implements Board, Position {

    private final Cell[][] cells;
    private final int n, m, k, p;
    private final CellSet set;
    private int empty;
    private Cell turn;

    public MNKBoard(int m, int n, int k, int p) {
        if (k > m && k > n || m <= 0 || n <= 0 || k <= 0) {
            throw new AssertionError("Wrong set {m, n, k}");
        }
        this.n = n;
        this.m = m;
        this.k = k;
        this.p = p;
        this.cells = new Cell[n][m];
        this.set = new CellSet(p);
        this.empty = n * m;
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
    public Result makeMove(final Move move) {
        if (!isValid(move)) {
            return Result.LOSE;
        }

        cells[move.getRow()][move.getColumn()] = move.getValue();
        empty--;

        final int[][] dir = new int[4][0];
        dir[0] = new int[] {1, 0};
        dir[1] = new int[] {0, 1};
        dir[2] = new int[] {1, 1};
        dir[3] = new int[] {1, -1};
        for (int[] e : dir) {
            int sum = 1;
            for (int i = 2; i > 0; i--) {
                for (int r = move.getRow() + e[0], c = move.getColumn() + e[1]; r >= 0 && r < n && c >= 0 && c < m && cells[r][c] == turn; r += e[0], c += e[1]) {
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
        return 0 <= move.getRow() && move.getRow() < n
                && 0 <= move.getColumn() && move.getColumn() < m
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == move.getValue();
    }

    @Override
    public Cell getCell(int r, int c) {
        return cells[r][c];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int r = 0; r < n; r++) {
            sb.append("\n");
            sb.append(r);
            for (int c = 0; c < m; c++) {
                sb.append(set.get(cells[r][c]));
            }
        }
        return sb.toString();
    }

    @Override
    public int rows() {
        return n;
    }

    @Override
    public int columns() {
        return m;
    }

    @Override
    public int isRhombus() {
        return 0;
    }

    @Override
    public int countOfPlayers() {
        return p;
    }

}
