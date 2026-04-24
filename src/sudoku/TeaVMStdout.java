package sudoku;

import java.io.OutputStream;
import java.io.PrintStream;
import org.teavm.jso.JSExport;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

public final class TeaVMStdout {

	@JSFunctor
	public interface OutputHandler extends JSObject {
		void accept(String line, boolean isError);
	}

	private static OutputHandler handler;
	private static boolean installed;

	private TeaVMStdout() {}

	@JSExport
	public static void setHandler(OutputHandler newHandler) {
		handler = newHandler;
	}

	@JSExport
	public static OutputHandler getHandler() {
		return handler;
	}

	@JSExport
	public static void clearHandler() {
		handler = null;
	}

	static void install() {
		if (installed) {
			return;
		}
		System.setOut(new PrintStream(new LineOutputStream(false), true));
		System.setErr(new PrintStream(new LineOutputStream(true), true));
		installed = true;
	}

	private static void emit(String line, boolean isError) {
		OutputHandler currentHandler = handler;
		if (currentHandler == null) {
			return;
		}

		currentHandler.accept(line, isError);
	}

	private static final class LineOutputStream extends OutputStream {

		private final boolean error;
		private final StringBuilder buffer = new StringBuilder();

		private LineOutputStream(boolean error) {
			this.error = error;
		}

		@Override
		public void write(int value) {
			char ch = (char) (value & 0xff);
			if (ch == '\n') {
				emitLine();
			} else if (ch != '\r') {
				buffer.append(ch);
			}
		}

		@Override
		public void flush() {
			if (buffer.length() > 0) {
				emitLine();
			}
		}

		@Override
		public void close() {
			flush();
		}

		private void emitLine() {
			emit(buffer.toString(), error);
			buffer.setLength(0);
		}
	}
}