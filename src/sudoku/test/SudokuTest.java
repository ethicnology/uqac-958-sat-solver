package sudoku.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import sudoku.Sudoku;
import sudoku.SudokuSATSolver;
import sudoku.Sudoku.SudokuException;

/**
 * Test sudokus SAT solver system
 * 
 **************** USE ONLY JUNIT 5 !!!! ****************
 **************** USE ONLY JUNIT 5 !!!! ****************
 **************** USE ONLY JUNIT 5 !!!! ****************
 **************** USE ONLY JUNIT 5 !!!! ****************
 */
class SudokuTest {
	
	private static SudokuSATSolver solver;
	
	@BeforeAll
	static void init() throws SudokuException {
		solver = SudokuSATSolver.getInstance();
	}
	
	@Test
	@DisplayName("Test sudoku input invalide (longueur de chaîne)")
	void testInvalidSudokuStringLength() {
		String invalidSudoku = "##########";
		
		SudokuException exception = assertThrows(SudokuException.class, () -> {
			Sudoku sudoku = new Sudoku(invalidSudoku);
		});
		
		assertEquals(String.format(Sudoku.SUDOKU_STRING_LENGTH_ERROR, invalidSudoku.length()), exception.getMessage());
	}
	
	@Test
	@DisplayName("Test sudoku input invalide (mauvais caractères)")
	void testInvalidSudokuStringPattern() {
		String invalidSudoku = "?################################################################################";
		
		SudokuException exception = assertThrows(SudokuException.class, () -> {
			Sudoku sudoku = new Sudoku(invalidSudoku);
		});
		
		assertEquals(String.format(Sudoku.SUDOKU_STRING_PATTERN_ERROR, invalidSudoku), exception.getMessage());
	}
	
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideSudokuFail")
	@DisplayName("Test des échecs des conditions")
	void testSudokuUnresovable(String input, String testName) {
		SudokuException exception = assertThrows(SudokuException.class, () -> {
			Sudoku sudoku = new Sudoku(input);
			solver.solve(sudoku);
		});
		
		assertEquals(SudokuSATSolver.SOLVE_ERROR, exception.getMessage());
	}
	
	/**
	 * Returns sudoku that fails
	 */
	static Stream<Arguments> provideSudokuFail() {
	    return Stream.of(
  	      Arguments.of("11###############################################################################","Sudoku non respect condition 2 (ligne)"),
	      Arguments.of("1########1#######################################################################","Sudoku non respect condition 3 (colonne)"),
	      Arguments.of("1#######1########################################################################","Sudoku non respect condition 4 (case)")
	    );
	}
	

	@ParameterizedTest(name = "Sudoku {index}")
	@MethodSource("provideSudokuTestSource")
	@DisplayName("Test de sudoku que l'on peut résoudre")
	void testSudokuParametrized(String input, String output) {
		try {
			Sudoku sudoku = new Sudoku(input);
			solver.solve(sudoku);
			assertEquals(output, sudoku.getSudokuString());
		} catch (SudokuException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Returns 10 sudoku input and output
	 */
	static Stream<Arguments> provideSudokuTestSource() {
	    return Stream.of(
	      Arguments.of("#########7#9##85###8#5#######1#####8####61#2##2##8##59#13##78##857###9#######6###","265149783739628514184573296471295638598361427326784159613957842857432961942816375"),
	      Arguments.of("6##1#####1###4#8#6#9#######4####751##6#3####8##79#5##47###########8#39#55########","654138297172549836398276451423687519965314728817925364739451682241863975586792143"),
	      Arguments.of("#36#########39##48####5###9##84####5#######36####85#2#5#4####82##326#########497#","936841257257396148841752369168423795425179836379685421514937682793268514682514973"),
	      Arguments.of("##23###68###896###########3###1#265#6##53#9#751#######93#6###7#####7#13##########","192357468743896521865421793379182654628534917514769382931648275486275139257913846"),
	      Arguments.of("#21###8####3###9#5##98#23#####6#9####6#############24###4#5##8###2#6##9##8#9#15##","421395867873416925659872314247639158168524739935187246794253681512768493386941572"),
	      Arguments.of("82#69#######7#######92#318######8#9##679###35######6#####1###########7#2#3#####56","821694573543781269679253184215368497467912835398475621756129348984536712132847956"),
	      Arguments.of("#1#84#5#####1##87########13#2##7#9#58##21#########9##2#####729####9####874#######","617843529932165874458792613126374985895216437374589162581437296263951748749628351"),
	      Arguments.of("#52#########59#1#####4###3########1#16#85####84#1##52####3####62###1694##########","752631489438597162691428735523974618167852394849163527914385276285716943376249851"),
	      Arguments.of("4##837#26#####2##3###9######8#47#5###6###9##439#28###7##65##3##5#3##########4####","451837926679152843238964751182476539765319284394285617946521378513798462827643195"),
	      Arguments.of("####16##47###4#2#94##2####6###5#37######6#####529#1####1##9##2##75#######6###2###","529716834736845219481239576648523791197468352352971648813697425275184963964352187")
	    );
	}
}

