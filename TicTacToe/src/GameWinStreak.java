/**
 * Stores a static global configuration for the win streak required in the game.
 * This is shared across different game-related classes that rely on a consistent streak rule.
 */
public class GameWinStreak {
	private static final int DEFAULT_WIN_STREAK = 3;

	private static int winStreak = DEFAULT_WIN_STREAK;

	/**
	 * Sets the required win streak for the current game session.
	 *
	 * @param streak the number of consecutive marks required to win
	 */
	public static void setWinStreak(int streak) {
		winStreak = streak;
	}

	/**
	 * Retrieves the current win streak requirement.
	 *
	 * @return the number of marks required in a row to win
	 */
	public static int getWinStreak() {
		return winStreak;
	}
}
