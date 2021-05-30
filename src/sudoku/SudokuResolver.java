package sudoku;

import java.util.regex.Pattern;

/**
 * Resolve a Sudoku with propositional logic and an SAT solver
 */
class SudokuResolver {

	private String input;
	
	/**
	 * Create a SudokuResolver from an input puzzle string
	 * @param input
	 * @throws Exception throws an exception if the input string isn't valid
	 */
	SudokuResolver(String input) throws Exception {
		// verify that input is valid
		verifyPuzzleString(input);
		
		this.input = input;
	}
	
	/**
	 * Resolves the input puzzle string and returns the output puzzle string
	 * @return the output puzzle string (the resolved sudoku)
	 * @throws SudokuException if sudoku cannot be resolved
	 */
	String resolve() throws SudokuException {
		// TEMP
		return this.input;
	}
	
	
	
	
	////////////////////////////////////
	// UTILS
	
	/**
	 * Exception for sudokus
	 */
	static class SudokuException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public SudokuException(String errorMessage) {
	        super(errorMessage);
	    }
	}
	

	/**
	 * Verify that the passed puzzle string is valid (81 characters long and only valid characters)
	 * @param puzzleString the puzzle string to verify
	 * @throws SudokuException if not valid
	 */
	private static void verifyPuzzleString(String puzzleString) throws SudokuException {
		if(puzzleString.length() != 81)
			throw new SudokuException("The puzzle string must be 81 characters long. Got : " + puzzleString.length());
		
		if(!puzzlePattern.matcher(puzzleString).matches())
			throw new SudokuException("The puzzle string must only contains '#' and digits '1-9'. Got : " + puzzleString);
	}
	
	/**
	 * Pattern that validate an puzzle input (only '#' or digits '1-9')
	 */
	private static Pattern puzzlePattern = Pattern.compile("[#1-9]*");
}
