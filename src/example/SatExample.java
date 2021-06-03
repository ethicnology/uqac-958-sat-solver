package example;
import org.sat4j.core.*;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class SatExample
{
	/**
	 * Create a Boolean formula and convert it to CNF.
	 */
	public static void main(String[] args) 
	{
		final int MAXVAR = 1000000;
		final int NBCLAUSES = 500000;
		
		ISolver solver = SolverFactory.newDefault();
		
		// prepare the solver to accept MAXVAR variables. MANDATORY for MAXSAT solving
		solver.newVar(MAXVAR);
		solver.setExpectedNumberOfClauses(NBCLAUSES);
		// Feed the solver using Dimacs format, using arrays of int
		// (best option to avoid dependencies on SAT4J IVecInt)
		for (int i=0;i<NBCLAUSES;i++) {
			int [] clause = {1, -3, 7};
		  // get the clause from somewhere
		  // the clause should not contain a 0, only integer (positive or negative)
		  // with absolute values less or equal to MAXVAR
		  // e.g. int [] clause = {1, -3, 7}; is fine
		  // while int [] clause = {1, -3, 7, 0}; is not fine 
			try {
				solver.addClause(new VecInt(clause));
			} catch (ContradictionException e) {
				e.printStackTrace();
			}
		}
		
		// we are done. Working now on the IProblem interface
		IProblem problem = solver;
		try {
			if (problem.isSatisfiable()) {
				System.out.println(problem);
			} else {
				System.out.println(problem);
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
}