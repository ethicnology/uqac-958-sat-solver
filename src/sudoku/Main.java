package sudoku;

/**
 * Main class that uses the sudoku sat solver to solve a sudoku created from the input args
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
		
		// get the sudoku input string
		String sudokuInputString = args[0];
		
		// create the sudoku
		Sudoku sudoku = new Sudoku(sudokuInputString);
		
		// try to resolve the sudoku with the sudoku SAT solver
		SudokuSATSolver.getInstance().solve(sudoku);
		
		// get the resulting output string
		String sudokuOutputString = sudoku.getSudokuString();
		
		// print the sudoku output string (resolved sudoku)
		System.out.println(sudokuOutputString);

		// rich print for better visualization (FOR DEBUG PURPOSES)
		displayRichIOSudoku(sudokuInputString, sudokuOutputString);
	}
	
	
	
	
	
	////////////////////////////////////
	// UTILS
	
	/**
	 * Display next to each other, the input sudoku string and the output sudoku string passed in the parameters
	 */
	static private void displayRichIOSudoku(String input, String output) {
		System.out.println("\n");
		System.out.println("╔══ Input ════╦══ Output ═══╗");
		System.out.println("║             ║             ║");

		String[] splittedInput = splitSudokuString(input);
		String[] splittedOutput = splitSudokuString(output);
		
		for(int i = 0; i < splittedInput.length && i < splittedOutput.length; i++) {
			System.out.println("║  " + splittedInput[i] + "  ║  " + splittedOutput[i] + "  ║");
		}

		System.out.println("║             ║             ║");
		System.out.println("╚═════════════╩═════════════╝");
	}
	
	// split the passed sudoku string in chunks of length 9
	static private String[] splitSudokuString(String puzzle) {
		return puzzle.split("(?<=\\G.{9})");
	}
}
