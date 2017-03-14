package com.topdesk.cases.tictactoe;

import static com.topdesk.cases.tictactoe.CellLocation.BOTTOM_CENTRE;
import static com.topdesk.cases.tictactoe.CellLocation.BOTTOM_LEFT;
import static com.topdesk.cases.tictactoe.CellLocation.BOTTOM_RIGHT;
import static com.topdesk.cases.tictactoe.CellLocation.CENTRE_CENTRE;
import static com.topdesk.cases.tictactoe.CellLocation.CENTRE_LEFT;
import static com.topdesk.cases.tictactoe.CellLocation.CENTRE_RIGHT;
import static com.topdesk.cases.tictactoe.CellLocation.TOP_CENTRE;
import static com.topdesk.cases.tictactoe.CellLocation.TOP_LEFT;
import static com.topdesk.cases.tictactoe.CellLocation.TOP_RIGHT;
import static com.topdesk.cases.tictactoe.CellState.EMPTY;
import static com.topdesk.cases.tictactoe.CellState.OCCUPIED_BY_O;
import static com.topdesk.cases.tictactoe.CellState.OCCUPIED_BY_X;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.isEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import com.topdesk.cases.tictactoe.yoursolution.YourConsultant;

@SuppressWarnings("all")
public class ConsultantTest {

	private Consultant consultant;

	@Before
	public void setConsultant() {
		consultant = new YourConsultant();
	}

	// Tests for candidate

	@Test
	public void blocksForkCase1() {
		givenBoard(
				"X  ",
				" O ",
				"  X")
				.suggestionOf(consultant)
				.is(oneOf(TOP_CENTRE, BOTTOM_CENTRE, CENTRE_LEFT, CENTRE_RIGHT));
	}
	
	@Test
	public void blocksForkCase2() {
		givenBoard(
				"  X",
				"X  ",
				" O ")
				.suggestionOf(consultant)
				.is(oneOf(CENTRE_CENTRE, BOTTOM_LEFT));
	}
	
	@Test
	public void blocksForkCase3() {
		givenBoard(
				"O  ",
				"  X",
				" X ")
				.suggestionOf(consultant)
				.is(oneOf(TOP_CENTRE, TOP_RIGHT, CENTRE_LEFT, BOTTOM_LEFT));
	}
	
	@Test
	public void blocksForkCase4() {
		givenBoard(
				" X ",
				"O X",
				"XO ")
				.suggestionOf(consultant)
				.is(TOP_RIGHT);
	}
	
	@Test
	public void blocksForkCase5() {
		givenBoard(
				"OX ",
				" X ",
				" O ")
				.suggestionOf(consultant)
				.is(oneOf(CENTRE_LEFT, BOTTOM_LEFT, BOTTOM_RIGHT));
	}
	
	@Test
	public void mightSuggestAnyCell_OnEmptyBoard() {
		givenEmptyBoard()
				.suggestionOf(consultant)
				.is(oneOf(CellLocation.values()));
	}

	@Test
	public void suggestsLastRemainingCell_OnAlmostFullBoard() {
		givenBoard(
				"XOO",
				"OXX",
				" XO")
				.suggestionOf(consultant)
				.is(BOTTOM_LEFT);
	}
	
	@Test
	public void winsWhenPossible() {
		givenBoard(
				" XX",
				" XO",
				"OO ")
				.suggestionOf(consultant)
				.is(TOP_LEFT);
	}
	
	@Test
	public void avoidsLosingWhenPossible() {
		givenBoard(
				"X O",
				" XX",
				"  O")
				.suggestionOf(consultant)
				.is(CENTRE_LEFT);
	}
	
	@Test
	public void favorsWinningOverNotLosing() {
		givenBoard(
				"   ",
				"OO ",
				"XX ")
				.suggestionOf(consultant)
				.is(BOTTOM_RIGHT);
	}
	
	@Test
	public void forksWhenSafelyPossible() {
		givenBoard(
				"   ",
				"OXX",
				" O ")
				.suggestionOf(consultant)
				.is(oneOf(TOP_RIGHT, BOTTOM_RIGHT));
	}
	
	@Test
	public void preventsOpponentFromForking() {
		givenBoard(
				"X  ",
				"   ",
				"   ")
				.suggestionOf(consultant)
				.is(CENTRE_CENTRE);
	}
	
	@Test
	public void avoidsBadEarlyMove() {
		givenBoard(
				"   ",
				" XO",
				"   ")
				.suggestionOf(consultant)
				.is(not(CENTRE_LEFT));
	}
	
	@Test
	public void suggestsAdvantageousEarlyPosition() {
		givenBoard(
				"   ",
				" X ",
				"   ")
				.suggestionOf(consultant)
				.is(oneOf(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT));
	}
	
	@Test
	public void throwsException_OnDraw() {
		givenBoard(
				"XOX",
				"XOX",
				"OXO")
				.suggestionOf(consultant)
				.isThrowing(IllegalStateException.class);
	}
	
	@Test
	public void throwsException_WhenO_AlreadyWon() {
		givenBoard(
				"XOX",
				"XO ",
				" O ")
				.suggestionOf(consultant)
				.isThrowing(IllegalStateException.class);
	}
	
	@Test
	public void throwsException_WhenX_AlreadyWon() {
		givenBoard(
				"OXX",
				" XO",
				"XO ")
				.suggestionOf(consultant)
				.isThrowing(IllegalStateException.class);
	}
	
	
	
	// Test flow
	
	/* Why the {@link Context} and {@link Verifier} classes?
	*  They enable writing tests that read well. An application of the
	*  "Write the tests you'd want to read" - principle
	* */
	
	private static Context givenBoard(String row1, String row2, String row3) {
		return new Context(board(row1, row2, row3));
	}
	
	private static class Context {
		private final GameBoard board;
		
		public Context(GameBoard board) {
			this.board = board;
		}
		
		private Verifier suggestionOf(Consultant consultant) {
			return new Verifier(() -> consultant.suggest(board));
		}
	}
	
	private static Context givenEmptyBoard() {
		return givenBoard("   ", "   ", "   ");
	}
	
	private static class Verifier {
		private final Supplier<CellLocation> resultSupplier;
		
		public Verifier(Supplier<CellLocation> resultSupplier) {
			this.resultSupplier = resultSupplier;
		}
		
		public void is(Consumer<CellLocation> assertion) {
			assertion.accept(resultSupplier.get());
		}
		
		public void is(CellLocation location) {
			is(exactly(location));
		}
		
		public void isThrowing(Class<? extends Exception> exception) {
			try {
				resultSupplier.get();
				fail("Expected suggestion to throw " + exception.getName());
			}
			catch (Exception ex) {
				String message = "Expected suggestion to throw " + exception.getSimpleName() + " instead of " + ex.getClass().getSimpleName();
				assertEquals(message, exception, ex.getClass());
			}
		}
	}
	
	// Assertions
	
	/* Assertion methods are separated from the {@link Verifier} class in order to make it easy for test writers to extend
	*  assertions.
	*  This pattern is described in the first 15 min of this video from Devoxx:
	*  https://www.youtube.com/watch?v=e4MT_OguDKg
	* */
	
	private static <T> Consumer<T> exactly(T value) {
		return result -> assertEquals("Expected " + value + " but suggestion was: " + result, value, result);
	}
	
	private static <T> Consumer<T> oneOf(T... values) {
		return result -> assertTrue(
				"Expected one of: " + Arrays.toString(values) + " but suggestion was: " + result,
				Arrays.stream(values).anyMatch(isEqual(result)));
	}
	
	private static <T> Consumer<T> not(T invalidValue) {
		return result -> assertNotEquals("Expected any suggestion but " + invalidValue, invalidValue, result);
	}
	
	// Utils
	
	private static GameBoard board(String... boardText) {
		return location -> BOARD_DATA
				.get(requireNonNull(location))
				.stateOn(boardText);
	}
	
	@FunctionalInterface
	private interface CellData {
		CellState stateOn(String[] board);
	}
	
	private static CellData cellDataOf(int columnIndex, int rowIndex) {
		return rows -> {
			switch (rows[rowIndex].charAt(columnIndex)) {
				case 'X': return OCCUPIED_BY_X;
				case 'O': return OCCUPIED_BY_O;
				default: return EMPTY;
			}
		};
	}
	
	private static final Map<CellLocation, CellData> BOARD_DATA = new EnumMap<>(CellLocation.class);
	
	static {
		BOARD_DATA.put(TOP_LEFT, cellDataOf(0, 0));
		BOARD_DATA.put(TOP_CENTRE, cellDataOf(1, 0));
		BOARD_DATA.put(TOP_RIGHT, cellDataOf(2, 0));
		
		BOARD_DATA.put(CENTRE_LEFT, cellDataOf(0, 1));
		BOARD_DATA.put(CENTRE_CENTRE, cellDataOf(1, 1));
		BOARD_DATA.put(CENTRE_RIGHT, cellDataOf(2, 1));
		
		BOARD_DATA.put(BOTTOM_LEFT, cellDataOf(0, 2));
		BOARD_DATA.put(BOTTOM_CENTRE, cellDataOf(1, 2));
		BOARD_DATA.put(BOTTOM_RIGHT, cellDataOf(2, 2));
	}
}
