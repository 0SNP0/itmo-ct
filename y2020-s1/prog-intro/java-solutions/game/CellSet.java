package game;

import java.util.Map;

public class CellSet {

    private final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.H, '-',
            Cell.I, '|',
            Cell.E, '.'
    );

    private final int count;

    public CellSet(int count) {
        if (count < 2 || count > 4) {
            throw new AssertionError("The number of players can be from 2 to 4");
        }
        this.count = count;
    }

    public Cell next(Cell cell) {
        switch (cell) {
            case X:
                return Cell.O;
            case O:
                if (count == 2) {
                    return Cell.X;
                } else {
                    return Cell.H;
                }
            case H:
                if (count == 3) {
                    return Cell.X;
                } else {
                    return Cell.I;
                }
            case I:
                return Cell.X;
            default:
                throw new AssertionError(get(cell) + " is not player turn");
        }
    }

    public char get(Cell cell) {
        return SYMBOLS.get(cell);
    }
}
