/**
 * Contains static logic for checking if a winning streak exists on the board
 * for a given player mark and win condition.
 * This class is stateless and meant to be used as a utility to avoid code duplication.
 */
public class GameWinLogic {

	/**
	 * Checks if the given mark has a winning streak on the board.
	 *
	 * @param board the board to check
	 * @param mark the mark (X or O) to evaluate
	 * @param winStreak the number of consecutive marks required to win
	 * @return true if a winning streak is found, false otherwise
	 */
	public static boolean hasWinningStreak(Board board, Mark mark, int winStreak) {
		int size = board.getSize();

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				// Check in all 4 directions
				if (checkDirection(board, row, col, 0, 1, mark, winStreak) ||    // Horizontal
						checkDirection(board, row, col, 1, 0, mark, winStreak) ||    // Vertical
						checkDirection(board, row, col, 1, 1, mark, winStreak) ||    // Diagonal \
						checkDirection(board, row, col, 1, -1, mark, winStreak)) {   // Diagonal /
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks a streak of consecutive marks from a starting position in a direction.
	 *
	 * @param board the board
	 * @param startRow starting row
	 * @param startCol starting column
	 * @param rowStep change in row per step
	 * @param columnStep change in column per step
	 * @param mark the mark to match
	 * @param winStreak the required consecutive count
	 * @return true if all consecutive cells contain the mark
	 */
	public static boolean checkDirection(Board board, int startRow, int startCol, int rowStep, int columnStep,
										  Mark mark, int winStreak) {
		int size = board.getSize();
		for (int i = 0; i < winStreak; i++) {
			int currentRow = startRow + (i * rowStep);
			int currentCol = startCol + (i * columnStep);

			if (currentRow < 0 || currentRow >= size || currentCol < 0 || currentCol >= size) {
				return false;
			}
			if (board.getMark(currentRow, currentCol) != mark) {
				return false;
			}
		}
		return true;
	}

}
