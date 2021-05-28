package sudoku;

/**
 * Main class that demonstrate the sudoku resolver
 * 
 * @author Léo Monteiro (CODE PERMANENT)
 * @author Jules Emery (CODE PERMANENT)
 * @author Antoine Bouabana (BOUA25119908)
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {
		// verify that an agurment has been given to the main
		if(args.length != 1)
			throw new Exception("You must pass exactly 1 parameter : the sudoku input string ! Got : " + args.length);
		
		// get the input string
		String inputSudoku = args[0];
		
		// create the sudoku resolver from the input string
		SudokuResolver resolver = new SudokuResolver(inputSudoku);
		
		// resolve the sudoku puzzle
		String outputSudoku = resolver.resolve();
		
		// print output puzzle string
		System.out.println(outputSudoku);
		
		
		// rich print for better visualization
		displayRichIOSudoku(inputSudoku, outputSudoku);
	}
	
	
	
	
	
	////////////////////////////////////
	// UTILS
	
	/**
	 * Display next to each other, the initial (input) and resolved (output) sudoku passed in the parameters
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
	
	/**
	 * Split the passed sudoku string in chunks of length 9
	 * @param puzzle string to split
	 * @return array of chunks
	 */
	static private String[] splitSudokuString(String puzzle) {
		return puzzle.split("(?<=\\G.{9})");
	}
}
