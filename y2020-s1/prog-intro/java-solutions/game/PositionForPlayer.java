package game;

public class PositionForPlayer implements Position{

    private Position board;

    public PositionForPlayer(Position board) {
        this.board = board;
    }

    @Override
    public boolean isValid(Move move) {
        return board.isValid(move);
    }

    @Override
    public Cell getCell(int r, int c) {
        return board.getCell(r, c);
    }

    @Override
    public int rows() {
        return board.rows();
    }

    @Override
    public int columns() {
        return board.columns();
    }

    @Override
    public int isRhombus() {
        return board.isRhombus();
    }

    @Override
    public String toString() {
        return board.toString();
    }
}
