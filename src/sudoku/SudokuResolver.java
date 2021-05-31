package sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import stev.booleans.And;
import stev.booleans.BooleanFormula;
import stev.booleans.Not;
import stev.booleans.Or;
import stev.booleans.PropositionalVariable;
import stev.booleans.Valuation;

/**
 * Resolve a Sudoku with propositional logic and an SAT solver
 */
class SudokuResolver {
	
	// all the variables
	private PropositionalVariable[][][] variables;

	// count of variable
	private int variableCount; 

	// the converted sudoku
	private int[][] sudoku;
	
	// the input string (sudoku)
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
		
		// converts the string to the actual sudoku
		this.sudoku = convertSudokuStringToSudoku(this.input);
		
		// create all the variables
		initVariables();
	}
	
	/**
	 * Resolves the input puzzle string and returns the output puzzle string
	 * @return the output puzzle string (the resolved sudoku)
	 * @throws SudokuException if sudoku cannot be resolved
	 */
	String resolve() throws SudokuException {

		/////////////////////////////////////////////////
		// 1 - GET BOOLEAN FORMULAS FOR 4 CONDITIONS
		
		// condition 1
		List<BooleanFormula> condition1 = this.generateFormulasForCondition1();

		// condition 2
		List<BooleanFormula> condition2 = this.generateFormulasForCondition2();
		
		// condition 3
		List<BooleanFormula> condition3 = this.generateFormulasForCondition3();
		
		// condition 4
		List<BooleanFormula> condition4 = this.generateFormulasForCondition4();
		
		
		/////////////////////////////////////////////////
		// 2 - EXTRACT ALL CLAUSES FROM THE CONDITIONS
				
		List<int[]> allClauses = extractAllClauses(condition1/*, condition2, condition3, condition4*/);
		

		/////////////////////////////////////////////////
		// 3 - CREATE SAT SOLVER AND INIT THE PROBLEM

		ISolver solver = SolverFactory.newDefault();
		
		// set variable count
		solver.newVar(variableCount);
		
		// set expected number of clauses
		solver.setExpectedNumberOfClauses(allClauses.size());
		
		// add all clauses to the SAT solver
		for(int[] clause: allClauses) {
			try {
				solver.addClause(new VecInt(clause));
			} catch (ContradictionException e) {
				e.printStackTrace();
			}
		}
		

		/////////////////////////////////////////////////
		// 4 - SOLVE THE PROBLEM
		
		IProblem problem = solver;
		
		boolean isSatisfiable = false;
		
		try {
			isSatisfiable = problem.isSatisfiable();
		}
		catch (TimeoutException e) {
			e.printStackTrace();
		}
		
		if(isSatisfiable) {
			// TODO
		}
		else throw new SudokuException("This sudoku cannot be resolved !");
		
		
		/////////////////////////////////////////////////////////////
		// DEBUG / TEST (USE THIS TO DISPLAY THINGS)
		
		// get the valuations
		Valuation valuations = convertSudokuToValuations(variables, sudoku);
		
		valuations.put("0,0#4", true); // change variable value
		debugPrintEvaluation(condition1, valuations); // display full valuation for condition1
		
		// TEMP
		return this.input;
	}
	
	/**
	 * Create all the variables (81 cells * 10 values per cell from 0 for empty to 9)
	 */
	private void initVariables() {
		this.variables = new PropositionalVariable[SUDOKU_SIZE][SUDOKU_SIZE][VALUES_COUNT];
		this.variableCount = 0;
		
		for(int i = 0; i < SUDOKU_SIZE; i++) {
			for(int j = 0; j < SUDOKU_SIZE; j++) {
				for(int k = 0; k < VALUES_COUNT; k++) {
					this.variables[i][j][k] = new PropositionalVariable(getVarNameFromIndices(i, j, k));
					this.variableCount++;
				}
			}
		}
	}
	
	/**
	 * Generate formulas for condition 1 (1 cell can only contain 1 value)
	 * @return the formulas
	 */
	private List<BooleanFormula> generateFormulasForCondition1() {
		List<BooleanFormula> formulas = new ArrayList<>();
		
		for(int i = 0; i < SUDOKU_SIZE; i++) {
			for(int j = 0; j < SUDOKU_SIZE; j++) {
				
				And valuesAnd[] = new And[VALUES_COUNT];
				
				for(int value = 0; value < VALUES_COUNT; value++) {
					Not notForValue[] = new Not[SUDOKU_SIZE];
					
					int actualK = 0;
					for(int k = 0; k < VALUES_COUNT; k++) {
						if(k == value) continue;
						
						notForValue[actualK] = new Not(this.variables[i][j][k]);
						actualK++;
					}
							
					valuesAnd[value] = new And(this.variables[i][j][value], new And(notForValue));
				}
				
				formulas.add(new Or(valuesAnd));
			}
		}
		
		return formulas;
	}
	
	/**
	 * Generate formulas for condition 2 (1 value per line)
	 * @return the formulas
	 */
	private List<BooleanFormula> generateFormulasForCondition2() {
		List<BooleanFormula> formulas = new ArrayList<>();
		return formulas;
	}
	
	/**
	 * Generate formulas for condition 3 (1 value per column)
	 * @return the formulas
	 */
	private List<BooleanFormula> generateFormulasForCondition3() {
		List<BooleanFormula> formulas = new ArrayList<>();
		return formulas;
	}
	
	/**
	 * Generate formulas for condition 4 (1 value per sub-grid of 3*3)
	 * @return the formulas
	 */
	private List<BooleanFormula> generateFormulasForCondition4() {
		List<BooleanFormula> formulas = new ArrayList<>();
		return formulas;
	}
	
	/**
	 * For the given lists of boolean formulas : convert them to CNF and returns all the clauses
	 * @param args lists of BooleanFormula to concatenate
	 * @return list of all the extracted clauses
	 */
	private static List<int[]> extractAllClauses(List<BooleanFormula> ... args) {
		List<int[]> clauses = new ArrayList<>();
		
		// for each array of formula
		for(List<BooleanFormula> formulas: args) {			
			// for each formula
			for(BooleanFormula formula: formulas) {
				// transform formula to CNF
				BooleanFormula cnf = BooleanFormula.toCnf(formula);
				
				// get clauses
				int[][] formulaClauses = cnf.getClauses();
				
				// concatenate clauses with the rest
				for(int[] clause : formulaClauses) {
					clauses.add(clause);
				}
			}
		}
		
		// return the clauses
		return clauses;
	}
	
	
	
	////////////////////////////////////
	// UTILS

	private static final int SUDOKU_SIZE = 9;
	private static final int VALUES_COUNT = 10;
	private static final int EMPTY_CELL = 0;
	
	/**
	 * Get the variable name from the indices
	 * @param i line of the sudoku
	 * @param j column of the sudoku
	 * @param k value of the cell at position [i, j]
	 * @return the variable name
	 */
	private static String getVarNameFromIndices(int i, int j, int k) {
		return "" + i + ',' + j + '#' + k;
	}
	
	/**
	 * Converts the string of the sudoku to a 2-dimensionnal array of integer
	 * @param sudoku string of the puzzle
	 * @return the converted sudoku
	 */
	private static int[][] convertSudokuStringToSudoku(String sudoku) {
		int[][] res = new int[SUDOKU_SIZE][SUDOKU_SIZE];
		
		for(int i = 0; i < sudoku.length(); i++) {
			Pos position = getPosFromId(i);
			int value;
			
			// convert the value to int
			try {
				value = Integer.parseInt(String.valueOf(sudoku.charAt(i)));
			}
			// if value is "#", error
			catch(NumberFormatException e) {
				value = EMPTY_CELL;
			}
			
			// add the value to the sudoku
			res[position.lin][position.col] = value;
		}
		
		return res;
	}
	
	/**
	 * From the sudoku and the variables, returns the valuation of all the variables
	 * @param variables all the variables
	 * @param sudoku (2-dimensional array of integer)
	 * @return the valuations
	 */
	private static Valuation convertSudokuToValuations(PropositionalVariable[][][] variables, int[][] sudoku) {
		Valuation valuations = new Valuation();
		
		// for all variables
		for(int i = 0; i < variables.length; i++) {
			for(int j = 0; j < variables[i].length; j++) {
				for(int k = 0; k < variables[i][j].length; k++) {
					String varName = variables[i][j][k].toString();
					boolean valuation = sudoku[i][j] == k;
					
					valuations.put(varName, valuation);
				}
			}
		}
		
		return valuations;
	}
	
	/**
	 * Returns the id of the cell from the position
	 * @param i line of the cell
	 * @param j column of the cell
	 * @return the id of the cell
	 */
	private static int getIdFromPos(int i, int j) {
		return i * SUDOKU_SIZE + j;
	}
	
	/**
	 * Gets the position of the cell from the id
	 * @param id
	 * @return
	 */
	private static Pos getPosFromId(int id) {
		return new Pos(Math.floorDiv(id, SUDOKU_SIZE), Math.floorMod(id, SUDOKU_SIZE));
	}
	
	/**
	 * Class that defines positions
	 */
	private static class Pos { public int lin, col; public Pos(int lin, int col) { this.lin = lin; this.col = col; } }
		
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
	

	///////////////////////////////////////////////////
	// DEBUG
	
	/**
	 * Print evaluation of every formulas in the passed array for the given valuation
	 */
	private static void debugPrintEvaluation(List<BooleanFormula> formulas, Valuation valuations) {
		for(int i = 0; i < formulas.size(); i++) {
			boolean evaluation = formulas.get(i).evaluate(valuations);
			System.out.println(i + " - v(x) = " + evaluation + " for x ==> " + formulas.get(i));
		}
	}
}
