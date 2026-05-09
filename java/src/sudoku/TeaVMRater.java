package sudoku;

import generator.SudokuGenerator;
import generator.SudokuGeneratorFactory;
import java.util.List;
import org.teavm.jso.JSExport;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;

public final class TeaVMRater {

	private TeaVMRater() {}

	@JSExport
	public static void rateSudoku(String puzzle, int maxScore, boolean includePath) {
		if (puzzle == null) {
			throw new IllegalArgumentException("puzzle must not be null");
		}

		TeaVMStdout.install();
		TeaVMStdout.clearCancellation();
		Options.initDefaults();

		String normalizedPuzzle = puzzle.trim();
		if (normalizedPuzzle.isEmpty()) {
			throw new IllegalArgumentException("puzzle must not be empty");
		}

		SudokuGenerator generator = SudokuGeneratorFactory.getDefaultGeneratorInstance();
		SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
		Sudoku2 sudoku = new Sudoku2();

		sudoku.setSudoku(normalizedPuzzle);
		boolean unsolvable = generator.getNumberOfSolutions(sudoku, 2) == 0;
		if (TeaVMStdout.isCancellationRequested()) {
			return;
		}

		solver.setSudoku(sudoku);
		solver.setMaxScore(maxScore >= 0 ? maxScore : Integer.MAX_VALUE);
		solver.solve();
		if (TeaVMStdout.isCancellationRequested()) {
			return;
		}

		List<SolutionStep> steps = solver.getSteps();
		boolean bruteForced = hasStep(steps, SolutionType.BRUTE_FORCE);
		boolean givenUp = !sudoku.isSolved() || hasStep(steps, SolutionType.GIVE_UP);

		TeaVMRatingStream.emit(
				normalizedPuzzle,
				1,
				solver.getLevel().getName(),
				solver.getScore(),
				givenUp,
				bruteForced,
				unsolvable,
				sudoku);

		if (!includePath) {
			return;
		}

		for (SolutionStep step : steps) {
			if (TeaVMStdout.isCancellationRequested()) {
				break;
			}
			TeaVMSolutionPathStream.emit(step);
		}
	}

	private static boolean hasStep(List<SolutionStep> steps, SolutionType type) {
		for (SolutionStep step : steps) {
			if (step.getType() == type) {
				return true;
			}
		}
		return false;
	}
}