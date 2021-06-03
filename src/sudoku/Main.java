package sudoku;

/**
 * Main class that uses the sudoku sat solver to solve a sudoku created from the input args
 * 
 * Set Debug.PRINT_DEBUG to true for extended logging
 * 
 * #####8####74###5##8#####4#9#1#4##2#89##1##7##2##37#9#########5##3#91###25#8##7###
 * 169548327374291586852763419617459238943182765285376941791824653436915872528637194
 * 
 * ####4###79#8#3########1#384####7###3######9#####9###4246#12######2##7###839##6###
 * 613845297948732156725619384296574813384261975571983642467128539152397468839456721
 * 
 * @author Léo Monteiro (CODE PERMANENT)
 * @author Jules Emery (EMEJ05119405)
 * @author Antoine Bouabana (BOUA25119908)
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {
		// verify that an agurment has been given to the main
		if(args.length != 1)
			throw new Exception("You must pass exactly 1 parameter : the sudoku input string ! Got : " + args.length);
		
		// DEBUG ONLY !!! (comment to disable Debug logging)
		Debug.PRINT_DEBUG = true;
		
		// get the sudoku input string
		String sudokuInputString = args[0];
		
		// create the sudoku
		Sudoku sudoku = new Sudoku(sudokuInputString);
		
		// try to resolve the sudoku with the sudoku SAT solver
		SudokuSATSolver.getInstance().solve(sudoku);
		
		// get the resulting output string
		String sudokuOutputString = sudoku.getSudokuString();
		
		// print the sudoku output string (resolved sudoku)
		if(!Debug.PRINT_DEBUG) System.out.println(sudokuOutputString);

		Debug.printDebug(getRichSudokuDisplay(sudokuInputString, sudokuOutputString));
		Debug.printDebug("\nResulting sudoku output : " + sudokuOutputString);
	}
	
	
	
	
	
	////////////////////////////////////
	// UTILS
	
	/**
	 * Returns a rich display of the two grid passed in parameters
	 */
	static private String getRichSudokuDisplay(String input, String output) {
		String res = "";

		res += "╔══ Input ════╦══ Output ═══╗\n";
		res += "║             ║             ║\n";

		String[] splittedInput = splitSudokuString(input);
		String[] splittedOutput = splitSudokuString(output);
		
		for(int i = 0; i < splittedInput.length && i < splittedOutput.length; i++) {
			res += "║  " + splittedInput[i] + "  ║  " + splittedOutput[i] + "  ║\n";
		}

		res += "║             ║             ║\n";
		res += "╚═════════════╩═════════════╝ ";
		
		return res;
	}
	
	// split the passed sudoku string in chunks of length 9
	static private String[] splitSudokuString(String puzzle) {
		return puzzle.split("(?<=\\G.{9})");
	}
}
