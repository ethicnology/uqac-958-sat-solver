package sudoku.utils;

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

	
	
	
	
	public static boolean PRINT_DEBUG = false;
	private static int PRINT_ID = -1;
	private static long lastTime = 0;
	
	public enum PrintMode {
		TIME, START, END
	}
	
	public static void printDebug(Object toPrint, PrintMode mode) {
		if(PRINT_DEBUG) {
			long elapsed = 0;
			
			if(lastTime == 0) lastTime = System.nanoTime();
			else {
				long current = System.nanoTime();
				elapsed = (current - lastTime) / 1000000;
				lastTime = current;
			}
			
			switch(mode) {
				case START:
					PRINT_ID = 1;
					System.out.println("\n=================================================");
					System.out.println("  " + PRINT_ID + " - DÉBUT - " + toPrint.toString());
					break;
					
				case END:
					System.out.println("  " + PRINT_ID + " - FIN - " + toPrint.toString());
					System.out.println("=================================================\n");
					PRINT_ID = -1;
					break;
					
				case TIME:
					System.out.println((PRINT_ID == -1 ? "" : ("  " + PRINT_ID + " - ")) + toPrint.toString() + (elapsed == 0 ? "" : " (" + elapsed + "ms)"));
					break;
			}
			
			if(PRINT_ID != -1) PRINT_ID++;
		}
	}
	
	public static void printDebug(Object toPrint) { printDebug(toPrint, PrintMode.TIME); }
}
