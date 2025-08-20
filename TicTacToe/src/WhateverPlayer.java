import java.util.Random;
/**
 * A player that plays in random order.
 */
public class WhateverPlayer implements Player {
	private Random rand = new Random();

	/**
	 * Constructor to initialize the player and the random number generator.
	 */
	public WhateverPlayer() {
		this.rand = new Random();
	}

	/**
	 * Method to play a turn by selecting a random valid move on the board.
	 * The method scans the board for blank spaces and then randomly chooses one of them to place the mark.
	 *
	 * @param board The game board to play on.
	 * @param mark The mark (X or O) the player will place on the board.
	 */
	@Override
	public void playTurn(Board board, Mark mark) {
		int size = board.getSize();
		int[][] validMoves = new int[size*size][2];
		int validMovesNumber = 0;

		// Loop through the board and find all valid (blank) positions.
		for (int row = 0; row < board.getSize(); row++) {
			for (int col = 0; col < board.getSize(); col++) {
				if (board.getMark(row, col) == Mark.BLANK) {
					validMoves[validMovesNumber][0] = row;
					validMoves[validMovesNumber][1] = col;
					validMovesNumber++;
				}
			}
		}

		// If there are valid moves, select one at random and place the mark.
		if (validMovesNumber > 0) {
			int randomIndex = rand.nextInt(validMovesNumber);
			int row = validMoves[randomIndex][0];
			int col = validMoves[randomIndex][1];
			board.putMark(mark, row, col);
		}
	}
}
