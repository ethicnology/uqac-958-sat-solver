package sudoku;

import stev.booleans.BooleanFormula;

public class Debug {

	public static void printBigFormula(BooleanFormula formula, int chunkSize) {
		String formulaString = formula.toString();
		
		for(int i = 0; i < formulaString.length(); i += chunkSize) {
			int indexEnd = Math.min(i + chunkSize, formulaString.length());
			System.out.println(formulaString.subSequence(i, indexEnd));
		}
	}
	
	public static void printBigFormula(BooleanFormula formula) {
		printBigFormula(formula, 300);
	}
}
