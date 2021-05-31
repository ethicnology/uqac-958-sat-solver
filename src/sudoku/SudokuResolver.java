package sudoku;

import java.util.regex.Pattern;

import stev.booleans.And;
import stev.booleans.BooleanFormula;
import stev.booleans.Implies;
import stev.booleans.Not;
import stev.booleans.Or;
import stev.booleans.PropositionalVariable;

/**
 * Resolve a Sudoku with propositional logic and an SAT solver
 */
class SudokuResolver {
	
	private PropositionalVariable[][][] variables;

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

		// create variables
		initVariables();
		
		BooleanFormula[] conditions1 = this.generateCondition1();
		
		for(int i = 0; i < conditions1.length; i++) {
			System.out.println(conditions1[i]);
		}
		
		return this.input;
	}
	
	private void initVariables() {
		this.variables = new PropositionalVariable[9][9][10];
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				for(int k = 0; k < 10; k++) {
					String varName = "" + i + ',' + j + '#' + k;
					this.variables[i][j][k] = new PropositionalVariable(varName);
				}
			}
		}
	}
	
	/**
	 * Generate condition 1 (1 cell can only contain 1 value)
	 * @return
	 */
	private BooleanFormula[] generateCondition1() {
		BooleanFormula[] formulas = new BooleanFormula[81];
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				
				int index = i * 9 + j;
				
				Implies impliesForCell[] = new Implies[10];
				
				for(int value = 0; value < 10; value++) {
					Not notForValue[] = new Not[9];
					
					int actualK = 0;
					for(int k = 0; k < 10; k++) {
						if(k == value) continue;
						
						notForValue[actualK] = new Not(this.variables[i][j][k]);
						actualK++;
					}
							
					impliesForCell[value] = new Implies(this.variables[i][j][value], new And(notForValue));
				}
				
				formulas[index] = new Or(impliesForCell);
			}
		}
		
		return formulas;
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
