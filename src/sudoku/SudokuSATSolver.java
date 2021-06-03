package sudoku;

import java.util.ArrayList;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import stev.booleans.PropositionalVariable;
import sudoku.Sudoku.Cell;
import sudoku.Sudoku.SudokuException;
import sudoku.utils.Debug;
import sudoku.utils.Debug.PrintMode;

/**
 * Resolves a given sudoku with a SAT solver and propositional logic based on SudokuPropositionalLogic
 */
public class SudokuSATSolver {
	
	private ISolver solver;
	
	/**
	 * Creates a SAT solver and initialize it with the sudoku propositional logic
	 */
	private SudokuSATSolver() throws SudokuException {
		// get the sudoku propositional logic instance
		SudokuPropositionalLogic sudokuLogic = SudokuPropositionalLogic.getInstance();
		
		// create a SAT solver
		this.solver = SolverFactory.newDefault();
		
		// set variable count
		this.solver.newVar(sudokuLogic.getPropositionalVariablesCount());
		
		// set expected number of clauses
		this.solver.setExpectedNumberOfClauses(sudokuLogic.getClausesCount());
		
		// add clauses to the SAT solver
		for(int[] clause: sudokuLogic.getClauses()) {
			try {
				this.solver.addClause(new VecInt(clause));
			} catch (ContradictionException error) {
				throw new SudokuException(INITIALIZATION_ERROR, error);
			}
		}
		
		Debug.printDebug("Création du solveur SAT à partir de la logique du sudoku");
	}
	
	/**
	 * Solves the given sudoku
	 * @param sudoku the sudoku to resolve
	 * @throws SudokuException if the sudoku cannot be resolved
	 */
	public void solve(Sudoku sudoku) throws SudokuException {
		Debug.printDebug("SudokuSATSolver.solve() - résolution du sudoku", PrintMode.START);
		
		// get the sudoku propositional logic instance
		SudokuPropositionalLogic sudokuLogic = SudokuPropositionalLogic.getInstance();
		
		// convert the solver to a problem
		IProblem problem = this.solver;
		
		// get the assumptions about the sudoku
		int[] assumptions = getAssumptionsFromSudoku(sudoku, sudokuLogic);
		Debug.printDebug("Récupération des valeurs du sudoku pour transfert au SAT");
		
		try {
			boolean isSatisfiable = problem.isSatisfiable(new VecInt(assumptions));
			Debug.printDebug("Vérification de la satisfaisabilité du problème");
			
			// if it is satisfiable, resolve the sudoku
			if(isSatisfiable) {
				// get the model
				int[] modelResult = problem.model();
				Debug.printDebug("Résolution du sudoku et récupération du modèle");

				// update the sudoku with the resulting model
				updateSudokuWithModel(sudoku, sudokuLogic, modelResult);
				Debug.printDebug("Application du modèle au sudoku");
			}
			// else, throw because the sudoku isn't resolvable
			else throw new SudokuException(SOLVE_ERROR);
		}
		catch (TimeoutException error) {
			throw new SudokuException(SATISFABILITY_ERROR, error);
		}

		Debug.printDebug("SudokuSATSolver.solve() - résolution du sudoku", PrintMode.END);
	}
	
	//////////////////////////////////////////////////////////////////////
	// SINGLETON
	
	// unique static instance of the class (singleton pattern)
	private static SudokuSATSolver instance = null;
	
	/**
	* Static function to access the singleton instance
	* @return the instance (creates it if not defined)
	*/
	public static SudokuSATSolver getInstance() throws SudokuException {
		if(instance == null) instance = new SudokuSATSolver();
		return instance;
	}
	
	
	
	//////////////////////////////////////////////////////////////////////
	// UTILS FUNCTIONS TO USE THE SUDOKU WITH THE SAT SOLVER
	
	/**
	 * Returns the assumptions for the given sudoku (converts sudoku values in DIMACS format values based on propositional variables and variables mapping)
	 */
	private static int[] getAssumptionsFromSudoku(Sudoku sudoku, SudokuPropositionalLogic sudokuLogic) {
		List<Integer> assumptions = new ArrayList<>();
		
		PropositionalVariable[][][] variables = sudokuLogic.getPropositionalVariables();
		
		// for each line
		for(int lin = 0; lin < variables.length; lin++) {
			// for each column
			for(int col = 0; col < variables[lin].length; col++) {
				int sudokuVal = sudoku.getCell(lin, col);
				
				// if the sudoku cell isn't empty, we set the values of the corresponding propositional variables
				if(sudokuVal != Sudoku.EMPTY_CELL) {
					
					// for each variable value
					for(int val = 0; val < variables[lin][col].length; val++) {
						String varName = variables[lin][col][val].toString();
						int dimacsVar = sudokuLogic.getDimacsFromVarName(varName);
						
						boolean isTrue = val == sudokuVal;
						
						if(isTrue) assumptions.add(dimacsVar);
						else assumptions.add(-dimacsVar);
					}
				}
			}
		}
		
		return assumptions.stream().mapToInt(Integer::intValue).toArray();
	}
	
	/**
	 * Updates the given sudoku with the given model
	 */
	private static void updateSudokuWithModel(Sudoku sudoku, SudokuPropositionalLogic sudokuLogic, int[] model) {
		// temporary mapping of all variables value
		boolean[][][] variableValues = new boolean[Sudoku.SIZE][Sudoku.SIZE][Sudoku.VALUES_COUNT];
		
		// for each variable of the model, assign it the the temporary variables value
		for(int modelVar : model) {
			boolean isTrue = modelVar > 0;
			int dimacs = Math.abs(modelVar);
			String varName = sudokuLogic.getVarNameFromDimacs(dimacs);
			Cell cell = sudokuLogic.getCellFromVarName(varName);
			
			variableValues[cell.lin][cell.col][cell.val] = isTrue;
		}
		
		// go through all the variables and assign values to the sudoku
		for(int lin = 0; lin < variableValues.length; lin++) {
			for(int col = 0; col < variableValues[lin].length; col++) {
				int countVar = 0;
				
				for(int val = 0; val < variableValues[lin][col].length; val++) {
					boolean varValue = variableValues[lin][col][val];
					
					// if variable value is true, assign it to the sudoku
					if(varValue) {
						sudoku.setCell(lin, col, val);
						countVar++;
					}
				}
				
				// if cell has no value at all or more than 1, error
				if(countVar != 1) sudoku.setCell(lin, col, Sudoku.ERROR_CELL);
			}
		}
	}
	
	
	
	//////////////////////////////////////////////////////////////////////
	// ERROR MESSAGES

	public static final String SOLVE_ERROR = "This sudoku cannot be resolved !";
	public static final String SATISFABILITY_ERROR = "An error occured while checking the satisfiability of the SAT problem";
	public static final String INITIALIZATION_ERROR = "An error occured while creating the SAT solver";
}
