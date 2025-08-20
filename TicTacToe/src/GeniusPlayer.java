/**
 * A player with the highest cleverness level, using advanced strategy logic.
 * This player delegates its decisions to a SmartPlayerSettings instance configured
 * for a higher challenge.
 */
public class GeniusPlayer implements Player {
	public static final int GENIUS_CLEVERNESS_LEVEL = 55;

	private SmartPlayerSettings smartStrategy;

	/**
	 * Constructs a GeniusPlayer with the predefined cleverness level.
	 */
	public GeniusPlayer() {
		this.smartStrategy = new SmartPlayerSettings(GENIUS_CLEVERNESS_LEVEL); // 100% smart
	}

	/**
	 * Plays a turn using smart strategy delegated to SmartPlayerSettings.
	 *
	 * @param board the board to play on
	 * @param mark the mark of the current player (X or O)
	 */
	@Override
	public void playTurn(Board board, Mark mark) {
		smartStrategy.playTurn(board, mark);
	}
}
