/**
 * Represents a game board of fixed size for placing marks (X, O, or BLANK).
 * Supports mark placement and retrieval while ensuring valid coordinates.
 */
public class Board {
	public static final int DEFAULT_BOARD_SIZE = 4;

	private int size;
	private Mark[][] board;

	/**
	 * Constructs a board with the default size.
	 */
	public Board() {
		this(DEFAULT_BOARD_SIZE);
	}

	/**
	 * Constructs a board with a custom size.
	 *
	 * @param size the dimensions of the square board
	 */
	public Board(int size) {
		this.size = size;
		this.board = new Mark[this.size][this.size];
		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {
				this.board[row][col] = Mark.BLANK;
			}
		}
	}

	/**
	 * @return the size of the board
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Attempts to place a mark at the given coordinates.
	 * Only succeeds if the coordinates are valid and the cell is empty.
	 *
	 * @param mark the mark to place (X or O)
	 * @param row the row index
	 * @param col the column index
	 * @return true if the mark was placed, false otherwise
	 */
	public boolean putMark(Mark mark, int row, int col) {
		// to undo a move (if needed)
		if (mark == Mark.BLANK && isValidCoordinate(row, col)) {
			this.board[row][col] = mark;
			return true;
		}
		else if (isValidCoordinate(row, col) &&
				(this.board[row][col] != Mark.O && this.board[row][col] != Mark.X) ) {
			this.board[row][col] = mark;
			return true;
		}
		return false;
	}

	/**
	 * Retrieves the mark at a given cell.
	 *
	 * @param row the row index
	 * @param col the column index
	 * @return the mark at the specified cell or BLANK if invalid
	 */
	public Mark getMark(int row, int col) {
		if (isValidCoordinate(row, col)) {
			return this.board[row][col];
		}
		return Mark.BLANK;
	}

	/**
	 * Checks whether the given coordinates are within the board bounds.
	 *
	 * @param row the row index
	 * @param col the column index
	 * @return true if valid, false otherwise
	 */
	private boolean isValidCoordinate(int row, int col) {
		return row >= 0 && col >= 0 && row < this.size && col < this.size;
	}
}
