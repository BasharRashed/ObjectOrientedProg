import java.util.Random;
public class SmartPlayerSettings{
	private int clevernessLevel;
	private int winStreak;
	private Random rand;
	private WhateverPlayer randomPlay;
	private int rowBlock, colBlock;

	public SmartPlayerSettings(int clevernessLevel) {
		this.clevernessLevel = clevernessLevel;
		this.winStreak = GameWinStreak.getWinStreak();
		this.rand = new Random();
		this.randomPlay = new WhateverPlayer();
		this.rowBlock = -1;
		this.colBlock = -1;
	}

	public void playTurn(Board board, Mark mark) {
		/** clever/genius players play correct moves clevernessLevel%
		 * 	of possible good turns, otherwise random move.
		 */
		int isClever = rand.nextInt(100);
		if (isClever < clevernessLevel) {
			// Place Mark at winnable situation
			boolean isWinMove = WinMove(board, mark);
			if (isWinMove) {
				board.putMark(mark, rowBlock, colBlock);
				return;
			}
			Mark opponent = Mark.BLANK;
			if(mark == Mark.X) {
				opponent = Mark.O;
			}
			else {
				opponent = Mark.X;
			}
			// try to block opponent when at winnable situation.
			boolean isOpWinMove = WinMove(board, opponent);
			if (isOpWinMove) {
				board.putMark(mark, rowBlock, colBlock);
				return;
			}
			else if (!isWinMove && !isOpWinMove) {
				randomPlay.playTurn(board, mark);
			}
		}
		else {
			randomPlay.playTurn(board, mark);
		}
	}

	private boolean WinMove(Board board, Mark mark) {
		boolean win = false;
		for (int row = 0; row < board.getSize(); row++) {
			for (int col = 0; col < board.getSize(); col++) {
				if (board.getMark(row, col) == Mark.BLANK) {
					board.putMark(mark, row, col);
					win = GameWinLogic.hasWinningStreak(board, mark,winStreak);
					//undo
					board.putMark(Mark.BLANK, row, col);
					if(win){
						rowBlock = row;
						colBlock = col;
						return true;
					}
				}
			}
		}
		return false;
	}
}
