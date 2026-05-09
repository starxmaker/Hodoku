package sudoku;

import org.teavm.jso.JSExport;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

public final class TeaVMSolutionPathStream {

	@JSFunctor
	public interface SolutionPathHandler extends JSObject {
		void accept(String technique, String notation, String actionsJson);
	}

	private static SolutionPathHandler handler;

	private TeaVMSolutionPathStream() {}

	@JSExport
	public static void setHandler(SolutionPathHandler newHandler) {
		handler = newHandler;
	}

	@JSExport
	public static SolutionPathHandler getHandler() {
		return handler;
	}

	@JSExport
	public static void clearHandler() {
		handler = null;
	}

	static void emit(SolutionStep step) {
		SolutionPathHandler currentHandler = handler;
		if (currentHandler != null) {
			currentHandler.accept(step.getType().getStepName(), formatNotation(step), formatActionsJson(step));
		}
	}

	private static String formatNotation(SolutionStep step) {
		String text = step.toString(2);
		int separatorIndex = text.indexOf(':');
		if (separatorIndex < 0) {
			return "";
		}
		return text.substring(separatorIndex + 1).trim();
	}

	private static String formatActionsJson(SolutionStep step) {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		boolean first = true;

		if (hasSetActions(step)) {
			first = appendSetActions(builder, step, first);
		} else {
			for (Candidate candidate : step.getCandidatesToDelete()) {
				first = appendAction(builder, first, "eliminate", candidate.getIndex(), candidate.getValue());
			}
		}

		builder.append(']');
		return builder.toString();
	}

	private static boolean hasSetActions(SolutionStep step) {
		return step.isSingle() || step.isForcingChainSet() || step.getType() == SolutionType.BRUTE_FORCE;
	}

	private static boolean appendSetActions(StringBuilder builder, SolutionStep step, boolean first) {
		if (step.getValues().isEmpty() || step.getIndices().isEmpty()) {
			return first;
		}

		if (step.getValues().size() == 1) {
			int value = step.getValues().get(0);
			for (int index : step.getIndices()) {
				first = appendAction(builder, first, "set", index, value);
			}
			return first;
		}

		int length = Math.min(step.getValues().size(), step.getIndices().size());
		for (int i = 0; i < length; i++) {
			first = appendAction(builder, first, "set", step.getIndices().get(i), step.getValues().get(i));
		}
		return first;
	}

	private static boolean appendAction(StringBuilder builder, boolean first, String type, int index, int value) {
		if (!first) {
			builder.append(',');
		}

		builder.append('{');
		builder.append("\"type\":\"").append(type).append("\",");
		builder.append("\"row\":").append(Sudoku2.getRow(index) + 1).append(',');
		builder.append("\"col\":").append(Sudoku2.getCol(index) + 1).append(',');
		builder.append("\"value\":").append(value);
		builder.append('}');
		return false;
	}
}