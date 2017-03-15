package com.topdesk.cases.tictactoe.yoursolution;
import com.topdesk.cases.tictactoe.CellLocation;
import com.topdesk.cases.tictactoe.CellState;
import com.topdesk.cases.tictactoe.Consultant;
import com.topdesk.cases.tictactoe.GameBoard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YourConsultant implements Consultant {




	@Override
	public CellLocation suggest(GameBoard gameBoard) throws NullPointerException, IllegalStateException {
//		check if the board is valid
		if (gameBoard == null) {
			throw new NullPointerException("gameBoard is empty");
		}
//			for iterating through the table
		CellState[][] boardOfStates = getBoardOfStates(gameBoard);
//			for counting empty cells and checking for game over
		List<CellState> allCellStates = new ArrayList<CellState>();
	//		check who's turn is it
		CellState currentPlayer = findCurrentPlayer(allCellStates);
	//		check if the game is over 1
		if (isGameOverNoEmptyCell(allCellStates)) throw new IllegalStateException("no available moves (board is full)");
	//		check if the game is over 2
		if (isGameOverAlreadyWon(allCellStates)) throw new IllegalStateException("game is already over" + currentPlayer.toString() + " won");

		int index = nextMove(currentPlayer,boardOfStates);

		CellLocation[] allCellLocations = CellLocation.values();

		return allCellLocations[index];
//	return null;
	}

	private CellState findCurrentPlayer(List allCellStates){
		if (Collections.frequency(allCellStates, CellState.OCCUPIED_BY_O) == Collections.frequency(allCellStates, CellState.OCCUPIED_BY_X)) {
			return CellState.OCCUPIED_BY_X;
		} else return CellState.OCCUPIED_BY_O;
	}

	private boolean isGameOverNoEmptyCell(List allCellStates){
		return (Collections.frequency(allCellStates, CellState.EMPTY) == 0);
	}
	private boolean isGameOverAlreadyWon(List allCellStates) {
		if (Collections.frequency(allCellStates, CellState.EMPTY) < 4) {
			// 		needs a few more checks to be a full solution: for top/bottom and north/south centre cell's neighbours
			for (int k = 1; k < 5; k++) {
				if (allCellStates.get(4) == allCellStates.get(4 + k) && allCellStates.get(4) == allCellStates.get(4 - k)) {
					return true;
				}
			}
		}
		return false;
	}

	private CellState[][] getBoardOfStates(GameBoard gameBoard){
		CellLocation[] allCellLocations = CellLocation.values();
//		List<CellState> allCellStates = new ArrayList<CellState>();
		CellState[][] boardOfStates = new CellState[3][3];
		int row = 0;
		int column = 0;
		for (CellLocation cellLocation : allCellLocations
				) {
//			allCellStates.add(gameBoard.getCellState(cellLocation));
			boardOfStates[row][column] = gameBoard.getCellState(cellLocation);
			if (column == 2) {
				column = 0;
				row++;
			} else {
				column++;
			}
		}
		return boardOfStates;
	}

	private CellState getBoardValue(int row, int column, CellState[][] boardOfStates){
		if (row < 0 || row >= 3 ) return CellState.EMPTY;
		if (column < 0 || column >= 3 ) return CellState.EMPTY;
		return boardOfStates[row][column];
	}

	/* calculate the winning move for current player */
	private int nextWinningMove(CellState player, CellState[][] boardOfStates) {
		for(int row = 0 ; row < 3 ; row++)
			for(int column = 0; column < 3; column++)
				if(boardOfStates[row][column] == CellState.EMPTY) {
					boardOfStates[row][column] = player;
					boolean win = isWin(player, boardOfStates);
					boardOfStates[row][column] = CellState.EMPTY;
					if(win) return 3*row+column;
				}
		return -1;
	}

	/* determine if current move is win or not win */
	private boolean isWin(CellState player, CellState[][] boardOfStates) {
		final int DI[] = {-1,0,1,1};
		final int DJ[] = {1,1,1,0};
		for (int row = 0; row < 3 ; row++)
			for (int column = 0 ; column < 3 ; column++) {

                /* we skip if the token in position(i,j) not equal current token */
				if (boardOfStates[row][column] != player) continue;

				for (int k = 0 ; k < 4 ; k++) {
					int ctr = 0;
					while(boardOfStates[row+DI[k]*ctr][column+DJ[k]*ctr] == player) ctr++;
					if(ctr == 3) return true;
				}
			}
		return false;
	}

	private int nextMove(CellState currentPlayer, CellState[][] boardOfStates) {

        /* if we can move on the next turn */
		int winMove = nextWinningMove(currentPlayer, boardOfStates);
		if (winMove != -1) return winMove;

        /* choose the move that prevent enemy to win */
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				if (getBoardValue(row, column, boardOfStates) == CellState.EMPTY) {
					boardOfStates[row][column] = currentPlayer;
					boolean ok = nextWinningMove(
							currentPlayer == CellState.OCCUPIED_BY_X ? CellState.OCCUPIED_BY_O : CellState.OCCUPIED_BY_X, boardOfStates) == -1;
					boardOfStates[row][column] = CellState.EMPTY;
					if (ok) return 3 * row + column;
				}
			}
		}

        /* lucky position in the center of board*/
		if (boardOfStates[1][1] == CellState.EMPTY) return 4;

        /* choose available move */
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				if (getBoardValue(row, column, boardOfStates) == CellState.EMPTY)
					return 3 * row + column;
			}
		}
        /* no move is available */
		return -1;
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




