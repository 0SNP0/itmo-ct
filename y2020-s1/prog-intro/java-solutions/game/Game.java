package game;

public class Game {
    private final boolean log;
    private final Player[] players;
    private boolean losers[];

    public Game(final boolean log, final Player[] players) {
        this.log = log;
        this.players = players;
        this.losers = new boolean[players.length];
    }

    public Game(final Player player1, final Player player2) {
        this(true, new Player[] {player1, player2});
    }

    public Game(final Player player1, final Player player2, Player player3) {
        this(true, new Player[] {player1, player2, player3});
    }

    public Game(final Player player1, final Player player2, Player player3, Player player4) {
        this(true, new Player[] {player1, player2, player3, player4});
    }

    public int play(Board board) {
        if (board.countOfPlayers() != players.length) {
            throw new AssertionError("Different number of players");
        }
        while (true) {
            for (int i = 0; i < players.length; i++) {
                int result = move(board, players[i], i + 1);
                if (result != -1) {
                    return result;
                }
            }
        }
    }

    private int move(final Board board, final Player player, final int no) {
        final Move move = player.move(board.getPosition(), board.getCell());
        final Result result = board.makeMove(move);
        log("\nPlayer " + no + " move: " + move);
        log("Position:" + board);
        if (result == Result.WIN) {
            log("Player " + no + " won");
            return no;
        } else if (result == Result.LOSE) {
            log("Player " + no + " lose");
            losers[no - 1] = true;
            int sum = 0, last = -1;
            for (int i = 0; i < losers.length; i++) {
                if (losers[i]) {
                    sum++;
                    last = i + 1;
                }
            }
            if (sum == 1) {
                return last;
            }
            return -1;
        } else if (result == Result.DRAW) {
            log("Draw");
            return 0;
        } else {
            return -1;
        }
    }

    private void log(final String message) {
        if (log) {
            System.out.println(message);
        }
    }
}
