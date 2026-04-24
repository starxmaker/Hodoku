let runtimePromise;

const DEFAULT_CONSOLE_HANDLER = (line, isError) => {
  const normalizedLine = typeof line === 'string' ? line : String(line);
  if (isError) {
    console.error(normalizedLine);
  } else {
    console.log(normalizedLine);
  }
};

function installDefaultConsoleHandler(runtime) {
  const runtimeStdout = runtime?.TeaVMStdout;
  if (
    typeof runtimeStdout?.setHandler === 'function'
    && typeof runtimeStdout?.getHandler === 'function'
    && runtimeStdout.getHandler() == null
  ) {
    runtimeStdout.setHandler(DEFAULT_CONSOLE_HANDLER);
  }

  return runtime;
}

async function loadRuntime() {
  if (!runtimePromise) {
    runtimePromise = import('./Hodoku-teavm.cjs').then((mod) => mod.default ?? mod);
  }

  return installDefaultConsoleHandler(await runtimePromise);
}

function createOutputCapture(runtime, onNewLine) {
  const lines = [];
  const runtimeStdout = runtime?.TeaVMStdout;

  if (typeof runtimeStdout?.setHandler !== 'function') {
    throw new Error('TeaVMStdout.setHandler is unavailable in this bundle. Rebuild HoDoKu and refresh Hodoku-teavm.cjs.');
  }

  const capture = (line) => {
    const normalizedLine = typeof line === 'string' ? line : String(line);
    lines.push(normalizedLine);
    if (onNewLine) {
      onNewLine(normalizedLine);
    }
  };

  const originalHandler = typeof runtimeStdout.getHandler === 'function'
    ? runtimeStdout.getHandler()
    : null;

  runtimeStdout.setHandler(capture);

  return {
    lines,
    restore() {
      if (originalHandler == null) {
        runtimeStdout.clearHandler();
        installDefaultConsoleHandler(runtime);
      } else {
        runtimeStdout.setHandler(originalHandler);
      }
    },
  };
}

function normalizeCommandPart(commandPart) {
  if (typeof commandPart !== 'string') {
    throw new TypeError('command parts must be strings');
  }

  const normalizedCommandPart = commandPart.trim();
  if (normalizedCommandPart.length === 0) {
    throw new TypeError('command parts must not be empty');
  }

  return normalizedCommandPart;
}

function normalizeCommandParts(commandParts) {
  if (!Array.isArray(commandParts)) {
    throw new TypeError('commandParts must be an array of strings');
  }

  return commandParts.map((part) => normalizeCommandPart(part));
}

export async function executeCommand(commandParts, onNewLine) {
  const normalizedCommandParts = normalizeCommandParts(commandParts);

  const runtime = await loadRuntime();
  const capture = createOutputCapture(runtime, onNewLine);

  try {
    await new Promise((resolve, reject) => {
      try {
        runtime.main(normalizedCommandParts, () => resolve());
      } catch (error) {
        reject(error);
      }
    });
  } finally {
    capture.restore();
  }

  return capture.lines;
}
