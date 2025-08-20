/**
 * Represents a full game session, managing players, turns, rendering, and win logic.
 * The game ends when one player wins or all board cells are filled.
 */
public class Game {
	private static final int DEFAULT_BOARD_SIZE = 4;
	private static final int DEFAULT_WIN_STREAK = 3;

	private Board board;
	private int winStreak;
	private Renderer renderer;
	private Player playerX;
	private Player playerO;
	private int maxMoves;

	/**
	 * Constructs a game with default board size and win streak.
	 *
	 * @param playerX the player using X mark
	 * @param playerO the player using O mark
	 * @param renderer the board renderer
	 */
	public Game(Player playerX, Player playerO, Renderer renderer) {
		this.board = new Board(DEFAULT_BOARD_SIZE);
		this.winStreak = DEFAULT_WIN_STREAK;
		this.renderer = renderer;
		this.playerX = playerX;
		this.playerO = playerO;
		this.maxMoves = DEFAULT_BOARD_SIZE * DEFAULT_BOARD_SIZE;
		GameWinStreak.setWinStreak(winStreak);
	}

	/**
	 * Constructs a game with a custom board size and win streak.
	 *
	 * @param playerX the player using X mark
	 * @param playerO the player using O mark
	 * @param size the board size
	 * @param winStreak the number of consecutive marks to win
	 * @param renderer the board renderer
	 */
	public Game(Player playerX, Player playerO, int size, int winStreak, Renderer renderer) {
		this.board = new Board(size);
		this.winStreak = winStreak;
		this.renderer = renderer;
		this.playerX = playerX;
		this.playerO = playerO;
		this.maxMoves = size * size;
		GameWinStreak.setWinStreak(winStreak);
	}

	/**
	 * @return the required win streak for this game
	 */
	public int getWinStreak() {
		return this.winStreak;
	}

	/**
	 * @return the board size of this game
	 */
	public int getBoardSize() {
		return this.board.getSize();
	}

	/**
	 * Runs the game loop until one player wins or all moves are used.
	 *
	 * @return the winning mark (X or O), or BLANK if it's a tie
	 */
	public Mark run() {
		Mark current = Mark.X;
		int moves = 0;

		while (true) {
			renderer.renderBoard(board);

			if (current == Mark.X) {
				playerX.playTurn(board, current);
			} else {
				playerO.playTurn(board, current);
			}

			moves++;

			if (GameWinLogic.hasWinningStreak(this.board,current,this.winStreak)) {
				renderer.renderBoard(board);
				return current;
			}

			if (moves == this.maxMoves) {
				renderer.renderBoard(board);
				return Mark.BLANK;
			}

			// Alternate turns
			if (current == Mark.X) {
				current = Mark.O;
			}
			else{
				current = Mark.X;
			}
		}
	}
}
