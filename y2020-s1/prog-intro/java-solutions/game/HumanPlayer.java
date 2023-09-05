package game;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class HumanPlayer implements Player {
    private final PrintStream out;
    private final Scanner in;

    public HumanPlayer(final PrintStream out, final Scanner in) {
        this.out = out;
        this.in = in;
    }

    public HumanPlayer() {
        this(System.out, new Scanner(System.in));
    }

    @Override
    public Move move(final Position position, final Cell cell) {
        while (true) {
            out.println("Position");
            out.println(position);
            out.println(cell + "'s move");
            out.println("Enter row and column");
            try {
                if (!in.hasNextInt()) {
                    throw new InputMismatchException();
                }
                final int row = in.nextInt();
                if (!in.hasNextInt()) {
                    throw new InputMismatchException();
                }
                final int column = in.nextInt();
                final Move move = new Move(row, column, cell);
                if (position.isValid(move)) {
                    return move;
                }
                out.println("Move " + move + " is invalid!\nTwo numbers must be coordinates of empty cell (.)\n");
            } catch (InputMismatchException e) {
                out.println("Incorrect input format!\nEnter two numbers that are the coordinates of empty cell (.)\n");
                in.nextLine();
            }
        }
    }
}
