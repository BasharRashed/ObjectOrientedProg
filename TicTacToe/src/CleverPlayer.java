import java.util.Random;

/**
 * A player that delegates its move decisions to a SmartPlayerSettings instance
 * with predefined configuration.
 * This player is intended to act with a consistent strategy using smart logic.
 */
public class CleverPlayer implements Player {
	private static final int SMARTNESS_LEVEL = 25;

	private SmartPlayerSettings smartStrategy;

	/**
	 * Constructs a CleverPlayer with a default smartness level.
	 */
	public CleverPlayer() {
		this.smartStrategy = new SmartPlayerSettings(SMARTNESS_LEVEL);
	}

	/**
	 * Makes a move on the board using the delegated smart strategy.
	 *
	 * @param board the game board
	 * @param mark the mark to be placed
	 */
	@Override
	public void playTurn(Board board, Mark mark) {
		smartStrategy.playTurn(board, mark);
	}
}
