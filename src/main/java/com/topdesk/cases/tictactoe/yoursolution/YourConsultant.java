package com.topdesk.cases.tictactoe.yoursolution;
import com.topdesk.cases.tictactoe.CellLocation;
import com.topdesk.cases.tictactoe.CellState;
import com.topdesk.cases.tictactoe.Consultant;
import com.topdesk.cases.tictactoe.GameBoard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YourConsultant implements Consultant {

	/* calculate the winning move for current token */
	public int []nextWinningMove(CellState currentPlayer, CellState[][] board) {
		for(int row = 0 ; row < 3 ; row++)
			for(int column = 0; column < 3; column++)
				if(board[row][column] == CellState.EMPTY) {
					board[row][column] = currentPlayer;
					boolean win = isWin(currentPlayer);
					board[row][column] = CellState.EMPTY;
					if(win) return new int[]{row,column};
				}
		return null;
	}

	/* determine if current token is win or not win */
	public boolean isWin(int token) {
		final int DI[]={-1,0,1,1};
		final int DJ[]={1,1,1,0};
		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++) {

                /* we skip if the token in position(i,j) not equal current token */
				if(getBoardValue(i, j)!=token) continue;

				for(int k=0;k<4;k++) {
					int ctr = 0;
					while(getBoardValue(i+DI[k]*ctr, j+DJ[k]*ctr)==token) ctr++;
					if(ctr==3) return true;
				}
			}
		return false;
	}


	@Override
	public CellLocation suggest(GameBoard gameBoard) throws NullPointerException, IllegalStateException {

		if (gameBoard == null) {
			throw new NullPointerException("gameBoard is empty");
		}

		CellState currentPlayer;
		CellLocation[] allCellLocations = CellLocation.values();
		int row = 0;
		int column = 0;
//			for iterating through the table
		CellState[][] boardOfStates = new CellState[3][3];
//			for easily getting the CellLocation to return
		CellLocation[][] boardOfLocations = new CellLocation[3][3];
//			for counting empty cells and checking for game over
		List<CellState> allCellStates = new ArrayList<CellState>();

		for (CellLocation cellLocation : allCellLocations
				) {
			allCellStates.add(gameBoard.getCellState(cellLocation));
			boardOfStates[row][column] = gameBoard.getCellState(cellLocation);
			boardOfLocations[row][column] = cellLocation;
			if (column == 2) {
				column = 0;
				row++;
			} else {
				column++;
			}
		}
	//		check who's turn is it
		if (Collections.frequency(allCellStates, CellState.OCCUPIED_BY_O) == Collections.frequency(allCellStates, CellState.OCCUPIED_BY_X)) {
			currentPlayer = CellState.OCCUPIED_BY_X;
		} else currentPlayer = CellState.OCCUPIED_BY_O;
	//		check if the game is over 1
		if (Collections.frequency(allCellStates, CellState.EMPTY) == 0)
			throw new IllegalStateException("no available moves (board is full)");
	//		check if the game is over 2
		if (Collections.frequency(allCellStates, CellState.EMPTY) < 4) {
			// needs a few more checks to be a full solution: for centre cell's neighbours
			for (int k = 1; k < 5; k++) {
				if (allCellStates.get(4) == allCellStates.get(4 + k) && allCellStates.get(4) == allCellStates.get(4 - k)) {
					throw new IllegalStateException("game is already over" + currentPlayer.toString() + " won");
				}
			}
		}

		return null;
	}
}


// ki jon?
//
// hova lepjen?
//CellLocation-t kell visszaadnia
//consultant.suggest(board)
//CellState getCellState(CellLocation cellLocation);
//gameBoard.getCellState(CellLocation);
//int numberOfx = 0;
//	int numberOfo = 0;
//	int numberOfempty =0;


//	if (boardOfStates[row][column] == CellState.EMPTY){
//			boardOfStates[row][column] = currentPlayer;


//	public CellState getBoardValue(int row,int column) {
//		if(row < 0 || row >= 3) return null;
//		if(column < 0 || column >= 3) return null;
//		return boardOf[i][j];
//	}




