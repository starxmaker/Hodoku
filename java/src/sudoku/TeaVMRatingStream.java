package sudoku;

import org.teavm.jso.JSExport;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

public final class TeaVMRatingStream {

	@JSFunctor
	public interface RatingHandler extends JSObject {
		void accept(String puzzle, int puzzleNumber, String difficulty, int score, boolean givenUp,
				boolean bruteForced, boolean unsolvable, String solution);
	}

	private static RatingHandler handler;
	private static boolean includeSolution;

	private TeaVMRatingStream() {}

	@JSExport
	public static void setHandler(RatingHandler newHandler) {
		handler = newHandler;
	}

	@JSExport
	public static RatingHandler getHandler() {
		return handler;
	}

	@JSExport
	public static void setIncludeSolution(boolean newIncludeSolution) {
		includeSolution = newIncludeSolution;
	}

	@JSExport
	public static boolean isIncludeSolution() {
		return includeSolution;
	}

	@JSExport
	public static void clearHandler() {
		handler = null;
	}

	static void emit(String puzzle, int puzzleNumber, String difficulty, int score, boolean givenUp,
			boolean bruteForced, boolean unsolvable, Sudoku2 sudoku) {
		RatingHandler currentHandler = handler;
		if (currentHandler != null) {
			currentHandler.accept(puzzle, puzzleNumber, difficulty, score, givenUp, bruteForced, unsolvable,
					includeSolution ? formatSolution(sudoku) : null);
		}
	}

	private static String formatSolution(Sudoku2 sudoku) {
		int[] solution = sudoku.getSolution();
		if (solution == null || solution.length != Sudoku2.LENGTH) {
			return null;
		}

		StringBuilder builder = new StringBuilder(solution.length);
		for (int value : solution) {
			if (value < 1 || value > 9) {
				return null;
			}
			builder.append((char) ('0' + value));
		}
		return builder.toString();
	}
}