/**
 * A human-controlled player that interacts with the user through console input.
 * This player prompts the user to enter a 2-digit number representing a board position.
 */

public class HumanPlayer implements Player {
	public static final String ENTER_COORDINATES_MSG =
			"Player %s, type coordinates: ";
	public static final String OCCUPIED_COORDINATES_MSG =
			"Mark position is already occupied. Please choose a valid position: ";
	public static final String INVALID_COORDINATES_MSG =
			"Invalid mark position. Please choose a valid position: ";

	/**
	 * Constructs a human player.
	 */
	public HumanPlayer() {
	}

	/**
	 * Prompts the user for input and places a mark on a valid and empty cell.
	 * Repeats until a valid move is entered.
	 *
	 * @param board the game board
	 * @param mark  the mark (X or O) to place
	 */
	@Override
	public void playTurn(Board board, Mark mark) {
		System.out.printf(ENTER_COORDINATES_MSG, mark);

		while (true) {
			int input = KeyboardInput.readInt();

			int row = input / 10; // tens digit = row
			int col = input % 10; // units digit = col

			if (board.putMark(mark, row, col)) {
				break;
			} else if (board.getMark(row, col) != Mark.BLANK) {
				System.out.print(OCCUPIED_COORDINATES_MSG);
			} else {
				System.out.print(INVALID_COORDINATES_MSG);
			}
		}
	}
}
