package sudoku;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Defines a sudoku as a two dimensional array created from an input string of 81 characters
 * it offers an interface to get and edit the sudoku's cells
 */
public class Sudoku {

	public static final int SIZE = 9; // size of the sudoku
	public static final int SQUARE_SIZE = 3; // size of a square in the sudoku (sub-grid)
	public static final int VALUES_COUNT = SIZE + 1; // number of values that the sudoku contains (1-9 + one value for empty cells)
	public static final int EMPTY_CELL = 0; // integer value of the empty cell
	public static final int ERROR_CELL = -1; // integer value of an error cell
	private static final char EMPTY_CHAR = '#'; // empty character for sudoku string encoding
	public static final char ERROR_CHAR = '!'; // error character for sudoku string encoding

	private int[][] sudoku;
	
	/**
	 * Create a sudoku from an input string
	 * @param sudokuString an input string
	 * @throws SudokuException if the input string isn't valid
	 */
	public Sudoku(String sudokuString) throws SudokuException {
		this.sudoku = convertStringToSudoku(sudokuString);
	}

	/**
	 * Returns the value of the cell
	 * @param lin index of the line
	 * @param col index of the column
	 * @return the value
	 */
	public int getCell(int lin, int col) {
		return this.sudoku[lin][col];
	}

	public int getCell(Pos pos) {
		return this.sudoku[pos.lin][pos.col];
	}
	
	/**
	 * Set the value of the given cell
	 * @param lin index of the line
	 * @param col index of the column
	 * @param value the value to set
	 * @return the value
	 */
	public void setCell(int lin, int col, int value) {
		this.sudoku[lin][col] = value;
	}
	
	public void setCell(Cell cell) {
		this.sudoku[cell.lin][cell.col] = cell.val;
	}
	
	/**
	 * Convert the sudoku to a string
	 * @return
	 */
	public String getSudokuString() {
		return convertSudokuToString(this.sudoku);
	}
	
	@Override
	public String toString() {
		return this.getSudokuString();
	}
	
	/**
	 * Exception for sudokus
	 */
	public static class SudokuException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public SudokuException(String errorMessage) {
	        super(errorMessage);
	    }
		
		public SudokuException(String errorMessage, Exception cause) {
	        super(errorMessage, cause);
	    }
	}
	
	// ERROR MESSAGES
	public static final String CONVERSION_ERROR = "An error occured while converting the sudoku !";
	public static final String SUDOKU_STRING_LENGTH_ERROR = "The puzzle string must be 81 characters long. Got : %d";
	public static final String SUDOKU_STRING_PATTERN_ERROR = "The puzzle string must only contains '#' and digits '1-9'. Got : %s";

	
	
	//////////////////////////////////////////////////////////////////////
	// UTILS FUNCTIONS TO GET POSITIONS IN SUDOKU
	
	/**
	 * Returns a list of position corresponding to the sub-grid (3x3 square) of the cell
	 * @param lin line of the cell
	 * @param col column of the cell
	 * @return list of position
	 */
	public static ArrayList<Pos> getSquare(int lin, int col) {
		ArrayList<Pos> squareCells = new ArrayList<>();
		
		int linDepart = lin - (lin % SQUARE_SIZE);
		int colDepart = col - (col % SQUARE_SIZE);
		
		for (int i = 0; i < SQUARE_SIZE; i++) {
			
			for (int j = 0; j < SQUARE_SIZE; j++) {
				squareCells.add(new Pos(linDepart + i, colDepart + j));		
			}			
		}
		
		return squareCells;
	}
	
	public static ArrayList<Pos> getSquare(Pos pos) { return getSquare(pos.lin, pos.col); }

	
	
	//////////////////////////////////////////////////////////////////////
	// UTILS FUNCTIONS TO CONVERT SUDOKUS
	
	/**
	 * Converts a string representing a sudoku to a sudoku (a 2-dimensionnal array of integer)
	 * @param sudokuString string to convert
	 * @return the converted sudoku
	 * @throws SudokuException if string isn't valid
	 */
	private static int[][] convertStringToSudoku(String sudokuString) throws SudokuException {
		verifySudokuString(sudokuString);
		
		int[][] res = new int[SIZE][SIZE];
		
		for(int index = 0; index < sudokuString.length(); index++) {
			Pos position = getPosFromIndex(index);
			
			// convert the value to int
			try {
				int value;
				char charAt = sudokuString.charAt(index);

				// convert the char to the good value
				if(charAt == EMPTY_CHAR) value = EMPTY_CELL;
				else value = Integer.parseInt(String.valueOf(charAt));
				
				// add the value to the sudoku
				res[position.lin][position.col] = value;
			}
			catch(NumberFormatException e) {
				throw new SudokuException(CONVERSION_ERROR);
			}
		}
		
		return res;
	}
	
	/**
	 * Converts a sudoku to a string
	 * @param sudoku
	 * @return
	 */
	private static String convertSudokuToString(int[][] sudoku) {
		String res = "";
		
		for(int[] line : sudoku) {
			for(int cell : line) {
				String value;
				
				switch(cell) {
					case EMPTY_CELL:
						value = String.valueOf(EMPTY_CHAR);
						break;

					case ERROR_CELL:
						value = String.valueOf(ERROR_CHAR);
						break;
						
					default:
						value = String.valueOf(cell);
						break;
				}
				
				res += value;
			}
		}
		
		return res;
	}
	
	/**
	 * Verify that the passed sudoku string is valid (81 characters long and only valid characters)
	 * @param sudokuString the sudoku string to verify
	 * @throws SudokuException if not valid (else does nothing)
	 */
	private static void verifySudokuString(String sudokuString) throws SudokuException {
		// pattern that validate an puzzle input (only '#' or digits '1-9')
		Pattern puzzlePattern = Pattern.compile("[#1-9]*");
		
		if(sudokuString.length() != 81)
			throw new SudokuException(String.format(SUDOKU_STRING_LENGTH_ERROR, sudokuString.length()));
		
		if(!puzzlePattern.matcher(sudokuString).matches())
			throw new SudokuException(String.format(SUDOKU_STRING_PATTERN_ERROR, sudokuString));
	}
	
	
	
	//////////////////////////////////////////////////////////////////////
	// UTILS FUNCTION FOR POSITION IN SUDOKU
	
	/**
	 * Gets the position of the cell from the index
	 * @param index
	 * @return the position
	 */
	private static Pos getPosFromIndex(int index) {
		return new Pos(Math.floorDiv(index, SIZE), Math.floorMod(index, SIZE));
	}
	
	/**
	 * Utils class that defines positions (line and column)
	 */
	public static class Pos { public final int lin, col; public Pos(int lin, int col) { this.lin = lin; this.col = col; } }
	
	/**
	 * Utils class that defines a cell (line, column and value)
	 */
	public static class Cell extends Pos { public final int val; public Cell(int lin, int col, int val) { super(lin, col); this.val = val; } }
}
