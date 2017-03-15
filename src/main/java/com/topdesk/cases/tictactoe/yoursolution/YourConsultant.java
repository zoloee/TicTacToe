package com.topdesk.cases.tictactoe.yoursolution;
import com.topdesk.cases.tictactoe.CellLocation;
import com.topdesk.cases.tictactoe.CellState;
import com.topdesk.cases.tictactoe.Consultant;
import com.topdesk.cases.tictactoe.GameBoard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YourConsultant implements Consultant{

	@Override
	public CellLocation suggest(GameBoard gameBoard) throws NullPointerException, IllegalStateException{

		if (gameBoard == null) throw new NullPointerException("gameBoard is empty");
		CellState[][] boardOfStates = getBoardOfStates(gameBoard);
		List<CellState> listOfStates = getListOfStates(gameBoard);
		CellState currentPlayer = getCurrentPlayer(listOfStates);
		if (isGameOverNoEmptyCell(listOfStates)) throw new IllegalStateException("no available moves (board is full)");
		if (isGameOverAlreadyWon(listOfStates)) throw new IllegalStateException("game is already over" + currentPlayer.toString() + " won");

		int index = nextMove(currentPlayer,boardOfStates);

		CellLocation[] allCellLocations = CellLocation.values();

		return allCellLocations[index];
	}

	private CellState getCurrentPlayer(List allCellStates){
		if (Collections.frequency(allCellStates, CellState.OCCUPIED_BY_O) == Collections.frequency(allCellStates, CellState.OCCUPIED_BY_X)){
			return CellState.OCCUPIED_BY_X;
		} else return CellState.OCCUPIED_BY_O;
	}

	private CellState getOtherPlayer(CellState currentPlayer){
		return currentPlayer == CellState.OCCUPIED_BY_X ? CellState.OCCUPIED_BY_O : CellState.OCCUPIED_BY_X;
	}

	private boolean isGameOverNoEmptyCell(List allCellStates){
		return (Collections.frequency(allCellStates, CellState.EMPTY) == 0);
	}
	private boolean isGameOverAlreadyWon(List allCellStates){
		if (Collections.frequency(allCellStates, CellState.EMPTY) < 4){
			// 		needs a few more checks to be a full solution: for left/right/top/bottom centre cell's neighbours
			for (int k = 1; k < 5; k++){
				if (allCellStates.get(4) == allCellStates.get(4 + k) && allCellStates.get(4) == allCellStates.get(4 - k)){
					return true;
				}
			}
		}
		return false;
	}

	private List<CellState> getListOfStates(GameBoard gameBoard){
		List<CellState> allCellStates = new ArrayList<>();
		for (CellLocation cellLocation : CellLocation.values()){
			allCellStates.add(gameBoard.getCellState(cellLocation));
		}
		return allCellStates;
	}

	private CellState[][] getBoardOfStates(GameBoard gameBoard){
		CellState[][] boardOfStates = new CellState[3][3];
		int row = 0;
		int column = 0;
		for (CellLocation cellLocation : CellLocation.values()){
			boardOfStates[row][column] = gameBoard.getCellState(cellLocation);
			if (column == 2){
				column = 0;
				row++;
			} else{
				column++;
			}
		}
		return boardOfStates;
	}

	/* get the CellValue from the board in a safe way */
	private CellState getCellValueByIndexes(int row, int column, CellState[][] boardOfStates){
		if (row < 0 || row >= 3 ) return null;
		if (column < 0 || column >= 3 ) return null;
		return boardOfStates[row][column];
	}

	/* calculate the winning move for current player */
	private int nextWinningMove(CellState player, CellState[][] boardOfStates){
		for(int row = 0 ; row < 3 ; row++)
			for(int column = 0; column < 3; column++)
				if(boardOfStates[row][column] == CellState.EMPTY){
					boardOfStates[row][column] = player;
					boolean win = isWin(player, boardOfStates);
					boardOfStates[row][column] = CellState.EMPTY;
					if(win) return 3 * row + column;
				}
		return -1;
	}

	/* determine if current move is win or not win */
	private boolean isWin(CellState player, CellState[][] boardOfStates){
		final int DI[] = {-1,0,1,1};
		final int DJ[] = {1,1,1,0};
		for (int row = 0; row < 3 ; row++)
			for (int column = 0 ; column < 3 ; column++){
				if (boardOfStates[row][column] != player) continue;
				for (int k = 0 ; k < 4 ; k++){
					int ctr = 0;
					while(getCellValueByIndexes((row + DI[k] * ctr),(column + DJ[k] * ctr), boardOfStates) == player) ctr++;
					if(ctr == 3) return true;
				}
			}
		return false;
	}


	private int nextForkingMove(CellState player, CellState[][] boardOfStates){
		for (int row = 0; row < 3; row++){
			for (int column = 0; column < 3; column++){
				if (getCellValueByIndexes(row, column, boardOfStates) == CellState.EMPTY){
					boolean ok = checkSurroundingForTwoNeighbourAndOneEmptyCell(row, column,boardOfStates,player);
					if (ok) return (3 * row + column);
				}
			}
		}
		return -1;
	}

	/* calculate the best possible move */
	private int nextMove(CellState currentPlayer, CellState[][] boardOfStates){

        /* if we can win */
		int winMove = nextWinningMove(currentPlayer, boardOfStates);
		if (winMove != -1) return winMove;

		/* prevent enemy to win */
		int otherWouldWinBy;
		for (int row = 0; row < 3; row++){
			for (int column = 0; column < 3; column++){
				if (getCellValueByIndexes(row, column, boardOfStates) == CellState.EMPTY){
					boardOfStates[row][column] = currentPlayer;
					otherWouldWinBy = nextWinningMove(getOtherPlayer(currentPlayer), boardOfStates);
					boardOfStates[row][column] = CellState.EMPTY;
					if (otherWouldWinBy != -1) return otherWouldWinBy;
				}
			}
		}

		/* if we can fork */
		int forkMove = nextForkingMove(currentPlayer,boardOfStates);
		if (forkMove != -1) return forkMove;

		/* if there is an oppotunity for a triplet */
		for (int row = 0; row < 3; row++){
			for (int column = 0; column < 3; column++){
				if (boardOfStates[row][column] == currentPlayer){
					forkMove = returnNextIndexForPossibleTriplets(row,column,boardOfStates);
					if (forkMove != -1) return forkMove;
				}
			}
		}

		/* prevent enemy to fork */
		int otherWouldForkBy;
		for (int row = 0; row < 3; row++){
			for (int column = 0; column < 3; column++){
				if (getCellValueByIndexes(row, column, boardOfStates) == CellState.EMPTY){
					boardOfStates[row][column] = currentPlayer;
					otherWouldForkBy = nextForkingMove(getOtherPlayer(currentPlayer), boardOfStates);
					boardOfStates[row][column] = CellState.EMPTY;
					if (otherWouldForkBy != -1) return otherWouldForkBy;
				}
			}
		}

        /* center of board if enemy does not win with this in the next move*/
		if (boardOfStates[1][1] == CellState.EMPTY){
			boardOfStates[1][1] = currentPlayer;
			boolean ok = nextWinningMove(getOtherPlayer(currentPlayer), boardOfStates) == -1;
			boardOfStates[1][1] = CellState.EMPTY;
			if (ok) return 4;
		}


        /* prevent enemy to fork 2 */
		for (int row = 0; row < 3; row++){
			for (int column = 0; column < 3; column++){
				if (getCellValueByIndexes(row, column, boardOfStates) == CellState.EMPTY){
					if (checkSurroundingForOneNeighbourAndOneEmptyCell(row,column,boardOfStates,getOtherPlayer(currentPlayer))){
						return 3 * row + column;
					}
				}
			}
		}

		/* choose available move */
		for (int row = 0; row < 3; row++){
			for (int column = 0; column < 3; column++){
				if (getCellValueByIndexes(row, column, boardOfStates) == CellState.EMPTY){
					return 3 * row + column;
				}
			}
		}

		/* no move is available */
		return -1;
	}

	private int returnNextIndexForPossibleTriplets(int row, int column, CellState[][] boardOfStates){
		final int DI[] = {0,-1,-1,-1,0,1,1,1};
		final int DJ[] = {-1,-1,0,1,1,1,0,-1};
		for (int k = 0 ; k < 8 ; k++){
			if (getCellValueByIndexes((row + DI[k]),(column + DJ[k]), boardOfStates) == CellState.EMPTY){
				if (getCellValueByIndexes((row + (DI[k] * 2)), (column + (DJ[k] * 2)), boardOfStates) == CellState.EMPTY){
					return 3 * (row + DI[k]) + (column + DJ[k]);
				}
			}
		}
		return -1;
	}

	private boolean checkSurroundingForOneNeighbourAndOneEmptyCell(
			int row, int column, CellState[][] boardOfStates, CellState player){
		final int DI[] = {0,-1,-1,-1,0,1,1,1};
		final int DJ[] = {-1,-1,0,1,1,1,0,-1};
		int ctr = 0;
		for (int k = 0 ; k < 8 ; k++){
			if (getCellValueByIndexes((row + DI[k]),(column + DJ[k]), boardOfStates) == player){
				if (getCellValueByIndexes((row + (DI[k] * 2)), (column + (DJ[k] * 2)), boardOfStates) == CellState.EMPTY){
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkSurroundingForTwoNeighbourAndOneEmptyCell(
			int row, int column, CellState[][] boardOfStates, CellState currentPlayer){
		final int DI[] = {0,-1,-1,-1,0,1,1,1};
		final int DJ[] = {-1,-1,0,1,1,1,0,-1};
		int ctr = 0;
		for (int k = 0 ; k < 8 ; k++){
			if (getCellValueByIndexes((row + DI[k]),(column + DJ[k]), boardOfStates) == currentPlayer){
				if (getCellValueByIndexes((row + (DI[k] * 2)), (column + (DJ[k] * 2)), boardOfStates) == CellState.EMPTY){
					ctr++;
				}
			}
			if(ctr == 2) return true;
		}
		return false;
	}
}
