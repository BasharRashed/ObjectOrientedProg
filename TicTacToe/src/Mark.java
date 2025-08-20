/**
 * Enum representing a mark placed on the board: X, O, or BLANK (unoccupied).
 * Provides a string representation for rendering purposes.
 */
public enum Mark {
	BLANK,X,O;

	/**
	 * Returns a printable representation of the mark.
	 */
	@Override
	public String toString() {
		switch (this) {
			case BLANK:
				return null;
			case X:
				return "X";
			case O:
				return "O";
			default:
				return null;
		}
	}

}


