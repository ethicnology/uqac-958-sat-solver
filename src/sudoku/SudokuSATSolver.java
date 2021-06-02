package sudoku;

import java.util.Arrays;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import stev.booleans.PropositionalVariable;
import sudoku.Sudoku.Cell;
import sudoku.Sudoku.SudokuException;

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
				throw new SudokuException("An error occured while creating the SAT solver", error);
			}
		}
	}
	
	/**
	 * Solves the given sudoku
	 * @param sudoku the sudoku to resolve
	 * @throws SudokuException if the sudoku cannot be resolved
	 */
	public void solve(Sudoku sudoku) throws SudokuException {
		// get the sudoku propositional logic instance
		SudokuPropositionalLogic sudokuLogic = SudokuPropositionalLogic.getInstance();
		
		// convert the solver to a problem
		IProblem problem = this.solver;
		
		// get the assumptions about the sudoku
		int[] assumptions = getAssumptionsFromSudoku(sudoku, sudokuLogic);
		
		try {
			// if it is satisfiable, resolve the sudoku
			if(problem.isSatisfiable(new VecInt(assumptions))) {
				// get the model
				int[] modelResult = problem.model();

				// update the sudoku with the resulting model
				updateSudokuWithModel(sudoku, sudokuLogic, modelResult);
			}
			// else, throw because the sudoku isn't resolvable
			else throw new SudokuException("This sudoku cannot be resolved !");
		}
		catch (TimeoutException error) {
			throw new SudokuException("An error occured while checking the satisfiability of the SAT problem", error);
		}
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
		int[] assumptions = new int[sudokuLogic.getPropositionalVariablesCount()];
		
		PropositionalVariable[][][] variables = sudokuLogic.getPropositionalVariables();
		
		int index = 0;
		
		// for each line
		for(int lin = 0; lin < variables.length; lin++) {
			// for each column
			for(int col = 0; col < variables[lin].length; col++) {
				// for each value
				for(int value = 0; value < variables[lin][col].length; value++) {
					String varName = variables[lin][col][value].toString();
					int dimacsVar = sudokuLogic.getDimacsFromVarName(varName);
					
					boolean isTrue = sudoku.getCell(lin, col) == value;
					
					if(isTrue) assumptions[index] = dimacsVar;
					else assumptions[index] = -dimacsVar;

					index++;
				}
			}
		}
		
		return assumptions;
	}
	
	/**
	 * Updates the given sudoku with the given model
	 */
	private static void updateSudokuWithModel(Sudoku sudoku, SudokuPropositionalLogic sudokuLogic, int[] model) {
		// for each variable of the model
		for(int modelVar : model) {
			// if the variable is true, we set the variable in the sudoku
			if(modelVar > 0) {
				int dimacs = Math.abs(modelVar);
				String varName = sudokuLogic.getVarNameFromDimacs(dimacs);
				Cell cell = sudokuLogic.getCellFromVarName(varName);
				
				// set the cell to the good value
				sudoku.setCell(cell);
			}
		}
	}
}
