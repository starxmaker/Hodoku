package sudoku;

import generator.SudokuGenerator;
import generator.SudokuGeneratorFactory;
import java.util.Arrays;
import java.util.List;
import org.teavm.jso.JSExport;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;

public final class TeaVMGenerator {

	private TeaVMGenerator() {}

	@JSExport
	public static void generateSudokus(int quantity, String difficulty, int minScore, int maxScore) {
		TeaVMStdout.install();
		TeaVMStdout.clearCancellation();
		Options.initDefaults();

		DifficultyLevel requestedDifficulty = resolveDifficultyLevel(difficulty);
		SudokuGenerator generator = SudokuGeneratorFactory.getInstance();
		SudokuSolver solver = SudokuSolverFactory.getInstance();
		boolean unbounded = quantity < 1;

		try {
			int emitted = 0;
			while ((unbounded || emitted < quantity) && !TeaVMStdout.isCancellationRequested()) {
				Sudoku2 generatedSudoku = generator.generateSudoku(false);
				if (generatedSudoku == null) {
					continue;
				}

				Sudoku2 ratedSudoku = generatedSudoku.clone();
				solver.setSudoku(ratedSudoku);
				solver.solve();

				DifficultyLevel generatedDifficulty = solver.getLevel();
				if (generatedDifficulty == null) {
					continue;
				}

				int generatedScore = solver.getScore();
				if (requestedDifficulty != null && generatedDifficulty.getOrdinal() != requestedDifficulty.getOrdinal()) {
					continue;
				}
				if (minScore >= 0 && generatedScore < minScore) {
					continue;
				}
				if (maxScore >= 0 && generatedScore > maxScore) {
					continue;
				}

				boolean bruteForced = hasStep(solver.getSteps(), SolutionType.BRUTE_FORCE);
				boolean givenUp = !ratedSudoku.isSolved() || hasStep(solver.getSteps(), SolutionType.GIVE_UP);
				if (ratedSudoku.isSolved()) {
					ratedSudoku.setSolution(Arrays.copyOf(ratedSudoku.getValues(), Sudoku2.LENGTH));
				} else {
					generator.validSolution(ratedSudoku);
				}

				TeaVMGenerationStream.emit(
						generatedSudoku.getSudoku(ClipboardMode.CLUES_ONLY),
						generatedDifficulty.getName(),
						generatedScore,
						givenUp,
						bruteForced,
						false,
						ratedSudoku);
				emitted++;
			}
		} finally {
			SudokuSolverFactory.giveBack(solver);
			SudokuGeneratorFactory.giveBack(generator);
			TeaVMStdout.clearCancellation();
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

	private static DifficultyLevel resolveDifficultyLevel(String difficulty) {
		if (difficulty == null) {
			return null;
		}

		String normalizedDifficulty = difficulty.trim();
		if (normalizedDifficulty.isEmpty()) {
			return null;
		}

		for (DifficultyLevel difficultyLevel : Options.getInstance().getDifficultyLevels()) {
			if (difficultyLevel.getType() != DifficultyType.INCOMPLETE
					&& difficultyLevel.getName().equalsIgnoreCase(normalizedDifficulty)) {
				return difficultyLevel;
			}
		}

		throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
	}
}