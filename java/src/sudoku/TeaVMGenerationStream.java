package sudoku;

import org.teavm.jso.JSExport;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

public final class TeaVMGenerationStream {

	@JSFunctor
	public interface GenerationHandler extends JSObject {
		void accept(String puzzle, String difficulty, int score, boolean givenUp, boolean bruteForced,
				boolean unsolvable, String solution);
	}

	private static GenerationHandler handler;

	private TeaVMGenerationStream() {}

	@JSExport
	public static void setHandler(GenerationHandler newHandler) {
		handler = newHandler;
	}

	@JSExport
	public static GenerationHandler getHandler() {
		return handler;
	}

	@JSExport
	public static void clearHandler() {
		handler = null;
	}

	static void emit(String puzzle, String difficulty, int score, boolean givenUp, boolean bruteForced,
			boolean unsolvable, Sudoku2 sudoku) {
		GenerationHandler currentHandler = handler;
		if (currentHandler != null) {
			currentHandler.accept(puzzle, difficulty, score, givenUp, bruteForced, unsolvable, formatSolution(sudoku));
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