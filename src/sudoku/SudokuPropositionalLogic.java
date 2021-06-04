package sudoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stev.booleans.And;
import stev.booleans.BooleanFormula;
import stev.booleans.Implies;
import stev.booleans.Not;
import stev.booleans.Or;
import stev.booleans.PropositionalVariable;
import sudoku.Sudoku.Cell;
import sudoku.utils.Debug;
import sudoku.utils.Debug.PrintMode;

/**
 * Singleton that defines the propositional logic behind a Sudoku (propositional variables and boolean formulas for the 4 conditions).
 *
 * Interface allow to get :
 * <ul>
 *  <li>the propositional variables</li>
 * 	<li>the final formulas</li>
 *  <li>the clauses</li>
 *  <li>the number of variables </li>
 *  <li>the number of clauses</li>
 * <ul>
 */
public class SudokuPropositionalLogic {

	// all propositional variables for a Sudoku (variables are indexed by lines, column and value of the cell from 0 to 9)
	private PropositionalVariable[][][] variables;
	
	// the final boolean formula (contains all the propositional logic for a sudoku)
	private BooleanFormula sudokuFormula;
	
	// the clauses of the final formula
	private int[][] clauses;

	private Map<String,Integer> variablesMap; // map of the variable (Propositional -> DIMACS)
	private Map<Integer, String> dimacsMap; // map of the variable (DIMACS -> Propositional)
	private Map<String, Cell> cellsMap; // map of the variables name to the corresponding cell (Propositional -> Cell)
	
	/**
	 * Generates the propositional logic behind a Sudoku
	 */
	private SudokuPropositionalLogic() {
		Debug.printDebug("SudokuPropositionalLogic() - création de la logique propositionelle du sudoku", PrintMode.START);
		
		// 1 - generate all the propositional variables
		this.generatePropositionalVariables();
		
		// 2 - generate the boolean formulas for the 4 conditions
		BooleanFormula formula1 = this.generateBooleanFormula1();
		BooleanFormula formula2 = this.generateBooleanFormula2();
		BooleanFormula formula3 = this.generateBooleanFormula3();
		BooleanFormula formula4 = this.generateBooleanFormula4();
		
		// 3 - combine all the formulas into the sudoku formula
		//this.sudokuFormula = new And(formula1); // TEMP (ajouter les formules à mesure qu'on les développe)
		this.sudokuFormula = new And(formula1, formula2, formula3, formula4);
		Debug.printDebug("Création et fusion des formules pour les 4 conditions du sudoku");
		
		// 4 - generate the clauses and the variables maps
		this.generateClausesAndVariablesMaps(this.sudokuFormula);

		Debug.printDebug("SudokuPropositionalLogic() - création de la logique propositionelle du sudoku", PrintMode.END);
	}
	
	/**
	 * Generate formula for condition 1 (1 cell can only contain 1 value)
	 * @return the formulas
	 */
	private BooleanFormula generateBooleanFormula1() {
		List<BooleanFormula> formulas = new ArrayList<>();
		// for each line
		for(int lin = 0; lin < Sudoku.SIZE; lin++) {
			// for each column
			for(int col = 0; col < Sudoku.SIZE; col++) {
				List<BooleanFormula> valuesAnd = new ArrayList<>();
				
				// for each value (to set to true)
				for(int val = 0; val < Sudoku.VALUES_COUNT; val++) {
					List<BooleanFormula> notValues = new ArrayList<>();
					
					if (val == 0) {
						notValues.add(new Not(this.variables[lin][col][val]));
					}
					
					// for each value (to set to false)
					for(int notVal = 0; notVal < Sudoku.VALUES_COUNT; notVal++) {
						if(notVal == val) continue; // add Not(value) except for the current value
						
						notValues.add(new Not(this.variables[lin][col][notVal]));
					}
							
					valuesAnd.add(new And(this.variables[lin][col][val], new And(notValues)));
				}
				
				formulas.add(new Or(valuesAnd));
			}
		}
		
		return new And(formulas);
	}	
	
	/**
	 * Generate formula for condition 2 (1 value per line)
	 * @return the formulas
	 */
	private BooleanFormula generateBooleanFormula2() {
		List<BooleanFormula> formulas = new ArrayList<>();		
		// for each line
		for(int lin = 0; lin < Sudoku.SIZE; lin++) {
			// for each column
			for(int col = 0; col < Sudoku.SIZE; col++) {
				
				// for each value possible
				for(int val = 0; val < Sudoku.VALUES_COUNT; val++) {
					List<BooleanFormula> valuesEligible = new ArrayList<>();
					
					// for each column on current line
					for(int deepness=0;  deepness<Sudoku.SIZE; deepness++) {
						if (deepness == col) continue;	//if we're looking at the same small square		
						
						//The value in the current square implies we can't use it in the other square of the same line
						valuesEligible.add(new Implies(this.variables[lin][col][val], new Not(this.variables[lin][deepness][val]))); 
					}
					
					//We add the restrictions to the formulas
					formulas.add(new And(valuesEligible));
				}
			}
		}
		
		return new And(formulas);
	}
	
	/**
	 * Generate formula for condition 3 (1 value per column)
	 * @return the formulas
	 */
	private BooleanFormula generateBooleanFormula3() {
		List<BooleanFormula> formulas = new ArrayList<>();	
		// for each line
		for(int lin = 0; lin < Sudoku.SIZE; lin++) {
			// for each column
			for(int col = 0; col < Sudoku.SIZE; col++) {
				
				// for each value possible in a square
				for(int val = 0; val < Sudoku.VALUES_COUNT; val++) {
					List<BooleanFormula> valuesEligible = new ArrayList<>();
					
					// for each line on current column
					for(int deepness=0;  deepness<Sudoku.SIZE; deepness++) {
						if (deepness == lin) continue; //if we're looking at the same small square we do nothing		
								
						//The value in the current square implies we can't use it in the other square of the same column
						valuesEligible.add(new Implies(this.variables[lin][col][val], new Not(this.variables[deepness][col][val])));
					}
					
					//We add the restrictions to the formulas
					formulas.add(new And(valuesEligible));
				}
			}
		}
		
		return new And(formulas);
	}
	
	/**
	 * Generate formula for condition 4 (1 value per sub-grid of 3*3)
	 * @return the formulas
	 */
	private BooleanFormula generateBooleanFormula4() {
		List<BooleanFormula> formulas = new ArrayList<>();
		// for each line
		for(int lin = 0; lin < Sudoku.SIZE; lin++) {
			// for each column
			for(int col = 0; col < Sudoku.SIZE; col++) {
				
				// for each value possible in a square
				for(int val = 0; val < Sudoku.VALUES_COUNT; val++) {
					List<BooleanFormula> valuesEligible = new ArrayList<>();
					
					//We get the 9 squares restriction for the current position (lin,col)
					ArrayList<Sudoku.Pos> square = Sudoku.getSquare(lin, col);	
					
					//For each square in the 9 squares restriction
					for(int deepness=0;  deepness<square.size(); deepness++) {
						
						/* Getting the position of the square to restrict */
						int deepLin = square.get(deepness).lin; 
						int deepCol = square.get(deepness).col;						
						if (deepLin == lin && deepCol == col) continue; //If the square is the same one as we check its value, we do nothing 
										
						//The value in the current square implies that we can't use it in the other square of the 9 squares restriction
						valuesEligible.add(new Implies(this.variables[lin][col][val], new Not(this.variables[deepLin][deepCol][val])));	
					}
					
					//We add the restrictions to the formulas
					formulas.add(new And(valuesEligible));
				}
			}
		}
		
		return new And(formulas);
	}

	/**
	 * Generates all the propositional variables (81 cells * 10 values per cell from 0 for empty to 9)
	 */
	private void generatePropositionalVariables() {
		this.variables = new PropositionalVariable[Sudoku.SIZE][Sudoku.SIZE][Sudoku.VALUES_COUNT];
		this.cellsMap = new HashMap<>();
		
		for(int lin = 0; lin < Sudoku.SIZE; lin++) {
			for(int col = 0; col < Sudoku.SIZE; col++) {
				for(int val = 0; val < Sudoku.VALUES_COUNT; val++) {
					String varName = getVarNameFromCell(lin, col, val);
					Cell cell = new Cell(lin, col, val);
					
					this.variables[lin][col][val] = new PropositionalVariable(varName);
					this.cellsMap.put(varName, cell);
				}
			}
		}
		
		Debug.printDebug("Création des variable propositionnelles");
	}

	/**
	 * Generates all the clauses from the final formula (passed as a parameter) and generate variables maps (normal and reversed)
	 */
	private void generateClausesAndVariablesMaps(BooleanFormula sudokuFormula) {
		// 1 - converts the formula to cnf
		BooleanFormula cnf = BooleanFormula.toCnf(sudokuFormula);
		Debug.printDebug("Transformation de la formule finale en CNF");

		// 2 - get the clauses from the converted boolean formula
		this.clauses = cnf.getClauses();
		Debug.printDebug("Extraction des clauses de la formule finale");
		
		// 3 - get the variables map (Propositional -> DIMACS)
		this.variablesMap = cnf.getVariablesMap();
		
		// 3 - generate the reversed variables map (DIMACS -> Propositional)
		this.dimacsMap = new HashMap<>();
		
		// reverse key -> value
		for(Map.Entry<String, Integer> entry : this.variablesMap.entrySet()){
			this.dimacsMap.put(entry.getValue(), entry.getKey());
		}
		
		Debug.printDebug("Génération des maps de variables");
	}
	
	
	
	////////////////////////////////
	// GETTERS

	public PropositionalVariable[][][] getPropositionalVariables() { return this.variables; }
	public BooleanFormula getSudokuFormula() { return this.sudokuFormula; }
	public int[][] getClauses() { return this.clauses; }
	
	public int getPropositionalVariablesCount() { return Sudoku.SIZE * Sudoku.SIZE * Sudoku.VALUES_COUNT; }
	public int getClausesCount() { return this.clauses.length; }
	
	/**
	 * Gets the propositional variable name from the Dimacs variable
	 * @param dimacs Dimacs variable
	 * @return the propositional variable name's
	 */
	public String getVarNameFromDimacs(int dimacs) { return this.dimacsMap.get(dimacs); }
	
	/**
	 * Gets the Dimacs variable from the propositional variable name
	 * @param varName propositional variable name
	 * @return Dimacs variable
	 */
	public int getDimacsFromVarName(String varName) { return this.variablesMap.get(varName); }
	
	/**
	 * Returns a cell (line, column and value) for the given variable name
	 * @param varName propositional variable name
	 * @return a cell
	 */
	public Sudoku.Cell getCellFromVarName(String varName) { return this.cellsMap.get(varName); }
	
	
	
	//////////////////////////////////////////////////////////////////////
	// SINGLETON
		
	// unique static instance of the class (singleton pattern)
	private static SudokuPropositionalLogic instance = null;
	
	/**
	 * Static function to access the singleton instance
	 * @return the instance (creates it if not defined)
	 */
	public static SudokuPropositionalLogic getInstance() {
		if(instance == null) instance = new SudokuPropositionalLogic();
		return instance;
	}
	
	
	
	//////////////////////////////////////////////////////////////////////
	// UTILS
	
	/**
	 * Get the variable name from the indices
	 * @param lin line of the sudoku
	 * @param col column of the sudoku
	 * @param val value of the cell at position [lin, col]
	 * @return the variable name
	 */
	private static String getVarNameFromCell(int lin, int col, int val) {
		return "" + lin + ',' + col + '#' + val;
	}
}
