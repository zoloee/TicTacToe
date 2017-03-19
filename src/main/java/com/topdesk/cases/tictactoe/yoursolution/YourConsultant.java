package com.topdesk.cases.tictactoe.yoursolution;
import com.topdesk.cases.tictactoe.CellLocation;
import com.topdesk.cases.tictactoe.CellState;
import com.topdesk.cases.tictactoe.Consultant;
import com.topdesk.cases.tictactoe.GameBoard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class YourConsultant implements Consultant{

	@Override
	public CellLocation suggest(GameBoard gameBoard) throws NullPointerException, IllegalStateException{

		if (gameBoard == null) throw new NullPointerException("gameBoard is empty");
		CellState[][] boardOfStates = getBoardOfStates(gameBoard);
		List<CellState> listOfStates = getListOfStates(gameBoard);
		CellState tokenForCurrentPlayer = getTokenForCurrentPlayer(listOfStates);
		if (isGameOverNoEmptyCell(listOfStates)) throw new IllegalStateException("no available moves (board is full)");
		if (isGameOverAlreadyWon(listOfStates)) throw new IllegalStateException("game is already over" + tokenForCurrentPlayer.toString() + " won");

		int index = nextMove(tokenForCurrentPlayer,boardOfStates);

		CellLocation[] allCellLocations = CellLocation.values();

		return allCellLocations[index];
	}

	private CellState getTokenForCurrentPlayer(List allCellStates){
		if (Collections.frequency(allCellStates, CellState.OCCUPIED_BY_O) == Collections.frequency(allCellStates, CellState.OCCUPIED_BY_X)){
			return CellState.OCCUPIED_BY_X;
		} else return CellState.OCCUPIED_BY_O;
	}

	private CellState getTokenForOtherPlayer(CellState tokenForCurrentPlayer){
		return tokenForCurrentPlayer == CellState.OCCUPIED_BY_X ? CellState.OCCUPIED_BY_O : CellState.OCCUPIED_BY_X;
	}

	private boolean isGameOverNoEmptyCell(List allCellStates){
		return (Collections.frequency(allCellStates, CellState.EMPTY) == 0);
	}

	private boolean isGameOverAlreadyWon(List allCellStates){
		if (Collections.frequency(allCellStates, CellState.EMPTY) < 4){

			/* check rows */
			int i = 0;
			while ( i <= 6 ){
				if (allCellStates.get(i) == allCellStates.get(i + 1) && allCellStates.get(i) == allCellStates.get(i + 2)) return true;
				i+=3;
			}

			/* check columns */
			i = 0;
			while ( i <= 2){
				if (allCellStates.get(i) == allCellStates.get(i + 3) && allCellStates.get(i) == allCellStates.get(i + 6)) return true;
				i++;
			}

			/* check traverses */
			i = 2;
			while ( i <= 3) {
				if (allCellStates.get(4) == allCellStates.get(4 + i) && allCellStates.get(4) == allCellStates.get(4 - i)) return true;
				i++;
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
	private CellState getCellState(int row, int column, CellState[][] boardOfStates){
		if (row < 0 || row >= 3) return null;
		if (column < 0 || column >= 3) return null;
		return boardOfStates[row][column];
	}

	/* calculate the winning move for player */
	private int nextWinningMove(CellState tokenForPlayer, CellState[][] boardOfStates){
		for (int row = 0 ; row < 3 ; row++)
			for (int column = 0; column < 3; column++)
				if(boardOfStates[row][column] == CellState.EMPTY){
					boardOfStates[row][column] = tokenForPlayer;
					boolean win = isWin(tokenForPlayer, boardOfStates);
					boardOfStates[row][column] = CellState.EMPTY;
					if (win) return 3 * row + column;
				}
		return -1;
	}

	/* determine if current move is win or not win */
	private boolean isWin(CellState tokenForPlayer, CellState[][] boardOfStates){
		final int DI[] = {-1,0,1,1};
		final int DJ[] = {1,1,1,0};
		for (int row = 0; row < 3 ; row++)
			for (int column = 0 ; column < 3 ; column++){
				if (boardOfStates[row][column] != tokenForPlayer) continue;
				for (int k = 0 ; k < 4 ; k++){
					int ctr = 0;
					while(getCellState((row + DI[k] * ctr),(column + DJ[k] * ctr), boardOfStates) == tokenForPlayer) ctr++;
					if (ctr == 3) return true;
				}
			}
		return false;
	}

	/* calculate a forking move for player */
	private int nextForkingMove(CellState tokenForPlayer, CellState[][] boardOfStates){
		for (int row = 0; row < 3; row++){
			for (int column = 0; column < 3; column++){
				if (getCellState(row, column, boardOfStates) == CellState.EMPTY){
					boolean ok = checkSurroundingForTwoNeighbourAndOneEmptyCell(row, column, boardOfStates, tokenForPlayer);
					if (ok) return 3 * row + column;
				}
			}
		}
		return -1;
	}

	/* calculate next move */
	private int nextMove(CellState tokenForCurrentPlayer, CellState[][] boardOfStates){
		CellState tokenForOtherPlayer = getTokenForOtherPlayer(tokenForCurrentPlayer);
		int indexForNextMove;

        /* if we can win */
		indexForNextMove = nextWinningMove(tokenForCurrentPlayer, boardOfStates);
		if (indexForNextMove != -1) return indexForNextMove;

		/* prevent otherPlayer from winning */
		for (int row = 0; row < 3; row++){
			for (int column = 0; column < 3; column++){
				if (getCellState(row, column, boardOfStates) == CellState.EMPTY){
					boardOfStates[row][column] = tokenForCurrentPlayer;
					/* check if otherPlayer could win with this */
					indexForNextMove = nextWinningMove(tokenForOtherPlayer, boardOfStates);
					boardOfStates[row][column] = CellState.EMPTY;
					if (indexForNextMove != -1) return indexForNextMove;
				}
			}
		}

		/* if we can fork */
		indexForNextMove = nextForkingMove(tokenForCurrentPlayer, boardOfStates);
		if (indexForNextMove != -1) return indexForNextMove;

		/* if there is an opportunity for a triplet */
		for (int row = 0; row < 3; row++){
			for (int column = 0; column < 3; column++){
				if (boardOfStates[row][column] == tokenForCurrentPlayer){
					indexForNextMove = returnNextIndexForPossibleTriplets(row,column,boardOfStates);
					if (indexForNextMove != -1) return indexForNextMove;
				}
			}
		}

		/* prevent otherPlayer to fork */
		for (int row = 0; row < 3; row++){
			for (int column = 0; column < 3; column++){
				if (getCellState(row, column, boardOfStates) == CellState.EMPTY){
					boardOfStates[row][column] = tokenForCurrentPlayer;
					indexForNextMove = nextForkingMove(tokenForOtherPlayer, boardOfStates);
					boardOfStates[row][column] = CellState.EMPTY;
					if (indexForNextMove != -1) return indexForNextMove;
				}
			}
		}

        /* center of board if otherPlayer can't win with this in the next turn */
		if (boardOfStates[1][1] == CellState.EMPTY){
			boardOfStates[1][1] = tokenForCurrentPlayer;
			boolean ok = nextWinningMove(tokenForOtherPlayer, boardOfStates) == -1;
			boardOfStates[1][1] = CellState.EMPTY;
			if (ok) return 4;
		}

        /* prevent otherPlayer to fork 2 */
		for (int row = 0; row < 3; row++){
			for (int column = 0; column < 3; column++){
				if (getCellState(row, column, boardOfStates) == CellState.EMPTY){
					if (checkSurroundingForOneNeighbourAndOneEmptyCell(row, column, boardOfStates, tokenForOtherPlayer)){
						return 3 * row + column;
					}
				}
			}
		}

		/* choose available move */
		for (int row = 0; row < 3; row++){
			for (int column = 0; column < 3; column++){
				if (getCellState(row, column, boardOfStates) == CellState.EMPTY){
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
			if (getCellState((row + DI[k]),(column + DJ[k]), boardOfStates) == CellState.EMPTY){
				if (getCellState((row + (DI[k] * 2)), (column + (DJ[k] * 2)), boardOfStates) == CellState.EMPTY){
					return 3 * (row + DI[k]) + (column + DJ[k]);
				}
			}
		}
		return -1;
	}

	private boolean checkSurroundingForOneNeighbourAndOneEmptyCell(
			int row, int column, CellState[][] boardOfStates, CellState tokenForPlayer){
		final int DI[] = {0,-1,-1,-1,0,1,1,1};
		final int DJ[] = {-1,-1,0,1,1,1,0,-1};
		int ctr = 0;
		for (int k = 0 ; k < 8 ; k++){
			if (getCellState((row + DI[k]),(column + DJ[k]), boardOfStates) == tokenForPlayer){
				if (getCellState((row + (DI[k] * 2)), (column + (DJ[k] * 2)), boardOfStates) == CellState.EMPTY){
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkSurroundingForTwoNeighbourAndOneEmptyCell(
			int row, int column, CellState[][] boardOfStates, CellState tokenForPlayer){
		final int DI[] = {0,-1,-1,-1,0,1,1,1};
		final int DJ[] = {-1,-1,0,1,1,1,0,-1};
		int ctr = 0;
		for (int k = 0 ; k < 8 ; k++){
			if (getCellState((row + DI[k]),(column + DJ[k]), boardOfStates) == tokenForPlayer){
				if (getCellState((row + (DI[k] * 2)), (column + (DJ[k] * 2)), boardOfStates) == CellState.EMPTY){
					ctr++;
				}
			}
			if (ctr == 2) return true;
		}
		return false;
	}
}
