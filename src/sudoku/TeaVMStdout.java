package sudoku;

import java.io.OutputStream;
import java.io.PrintStream;
import org.teavm.jso.JSBody;

final class TeaVMStdout {

	private TeaVMStdout() {}

	@JSBody(params = { "line", "isError" }, script = ""
			+ "if (globalThis.__hodokuStdout) {"
			+ "  globalThis.__hodokuStdout(line, !!isError);"
			+ "} else if (globalThis.console) {"
			+ "  if (isError && globalThis.console.error) {"
			+ "    globalThis.console.error(line);"
			+ "  } else if (globalThis.console.log) {"
			+ "    globalThis.console.log(line);"
			+ "  }"
			+ "}")
	private static native void emit(String line, boolean isError);

	static void install() {
		System.setOut(new PrintStream(new LineOutputStream(false), true));
		System.setErr(new PrintStream(new LineOutputStream(true), true));
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