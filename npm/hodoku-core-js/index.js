const RUNTIME_BUNDLE_URL = new URL('./Hodoku-teavm.cjs', import.meta.url);

let runtimeSourcePromise;

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

function isNodeRuntime() {
  return typeof process === 'object'
    && process != null
    && typeof process.versions === 'object'
    && process.versions != null
    && typeof process.versions.node === 'string';
}

async function readRuntimeSource() {
  if (isNodeRuntime()) {
    const { readFile } = await import('node:fs/promises');
    return readFile(RUNTIME_BUNDLE_URL, 'utf8');
  }

  if (typeof fetch !== 'function') {
    throw new Error('Unable to load the HoDoKu runtime source in this environment.');
  }

  const response = await fetch(RUNTIME_BUNDLE_URL);
  if (!response.ok) {
    throw new Error(`Unable to load ${RUNTIME_BUNDLE_URL.href}: ${response.status} ${response.statusText}`);
  }

  return response.text();
}

async function loadRuntimeSource() {
  if (!runtimeSourcePromise) {
    runtimeSourcePromise = readRuntimeSource();
  }

  return runtimeSourcePromise;
}

function instantiateRuntime(runtimeSource) {
  const runtime = new Function(
    'self',
    'exports',
    'define',
    `${runtimeSource}\n; return self;`,
  )({}, undefined, undefined);

  return installDefaultConsoleHandler(runtime);
}

function normalizeExecuteOptions(onNewLineOrOptions) {
  if (onNewLineOrOptions === undefined) {
    return {};
  }

  if (typeof onNewLineOrOptions === 'function') {
    return { onNewLine: onNewLineOrOptions };
  }

  if (onNewLineOrOptions === null || typeof onNewLineOrOptions !== 'object' || Array.isArray(onNewLineOrOptions)) {
    throw new TypeError('executeCommand options must be a function or an object');
  }

  const { onNewLine, signal } = onNewLineOrOptions;
  if (onNewLine !== undefined && typeof onNewLine !== 'function') {
    throw new TypeError('onNewLine must be a function');
  }

  if (
    signal !== undefined
    && (signal === null
      || typeof signal !== 'object'
      || typeof signal.aborted !== 'boolean'
      || typeof signal.addEventListener !== 'function'
      || typeof signal.removeEventListener !== 'function')
  ) {
    throw new TypeError('signal must be an AbortSignal');
  }

  return { onNewLine, signal };
}

function createOutputCapture(runtime, options) {
  const lines = [];
  const runtimeStdout = runtime?.TeaVMStdout;
  const { onNewLine, signal } = options;
  let cancelled = false;
  let abortListener = null;

  if (typeof runtimeStdout?.setHandler !== 'function') {
    throw new Error('TeaVMStdout.setHandler is unavailable in this bundle. Rebuild HoDoKu and refresh Hodoku-teavm.cjs.');
  }

  if (typeof runtimeStdout?.requestCancellation !== 'function' || typeof runtimeStdout?.clearCancellation !== 'function') {
    throw new Error('TeaVMStdout cancellation controls are unavailable in this bundle. Rebuild HoDoKu and refresh Hodoku-teavm.cjs.');
  }

  const cancel = () => {
    if (cancelled) {
      return;
    }

    cancelled = true;
    runtimeStdout.requestCancellation();
  };

  const controls = {
    cancel,
    get cancelled() {
      return cancelled;
    },
  };

  const capture = (line) => {
    const normalizedLine = typeof line === 'string' ? line : String(line);
    lines.push(normalizedLine);
    if (onNewLine) {
      const result = onNewLine(normalizedLine, controls);
      if (result === false) {
        cancel();
      }
    }
  };

  const originalHandler = typeof runtimeStdout.getHandler === 'function'
    ? runtimeStdout.getHandler()
    : null;

  runtimeStdout.clearCancellation();
  runtimeStdout.setHandler(capture);

  if (signal) {
    if (signal.aborted) {
      cancel();
    } else {
      abortListener = () => {
        cancel();
      };
      signal.addEventListener('abort', abortListener, { once: true });
    }
  }

  return {
    lines,
    wasCancelled() {
      return cancelled;
    },
    restore() {
      if (abortListener) {
        signal.removeEventListener('abort', abortListener);
      }

      runtimeStdout.clearCancellation();
      if (originalHandler == null) {
        runtimeStdout.clearHandler();
        installDefaultConsoleHandler(runtime);
      } else {
        runtimeStdout.setHandler(originalHandler);
      }
    },
  };
}

class RuntimeSlot {
  constructor(sourcePromise) {
    this.sourcePromise = sourcePromise;
    this.runtimePromise = null;
    this.busy = false;
  }

  async getRuntime() {
    if (!this.runtimePromise) {
      this.runtimePromise = this.sourcePromise.then((runtimeSource) => instantiateRuntime(runtimeSource));
    }

    return this.runtimePromise;
  }
}

class RuntimePool {
  constructor(options = {}) {
    const { size = 1 } = options;
    if (!Number.isInteger(size) || size < 1) {
      throw new RangeError('Runtime pool size must be a positive integer');
    }

    this.sourcePromise = loadRuntimeSource();
    this.slots = Array.from({ length: size }, () => new RuntimeSlot(this.sourcePromise));
    this.waiters = [];
    this.disposed = false;
  }

  async executeCommand(commandParts, onNewLineOrOptions) {
    if (this.disposed) {
      throw new Error('Runtime pool has been disposed');
    }

    const normalizedCommandParts = normalizeCommandParts(commandParts);
    const slot = await this.acquireSlot();
    let replaceRuntime = false;

    try {
      const runtime = await slot.getRuntime();
      const result = await executeCommandWithRuntime(runtime, normalizedCommandParts, onNewLineOrOptions);
      replaceRuntime = result.cancelled;
      return result.lines;
    } catch (error) {
      replaceRuntime = true;
      throw error;
    } finally {
      this.releaseSlot(slot, replaceRuntime);
    }
  }

  dispose() {
    if (this.disposed) {
      return;
    }

    this.disposed = true;
    while (this.waiters.length > 0) {
      const waiter = this.waiters.shift();
      waiter.reject(new Error('Runtime pool has been disposed'));
    }
  }

  async acquireSlot() {
    const availableSlot = this.slots.find((slot) => !slot.busy);
    if (availableSlot) {
      availableSlot.busy = true;
      return availableSlot;
    }

    return new Promise((resolve, reject) => {
      this.waiters.push({ resolve, reject });
    });
  }

  releaseSlot(slot, replaceRuntime) {
    if (this.disposed) {
      return;
    }

    let releasedSlot = slot;
    if (replaceRuntime) {
      const slotIndex = this.slots.indexOf(slot);
      releasedSlot = new RuntimeSlot(this.sourcePromise);
      this.slots[slotIndex] = releasedSlot;
    }

    const nextWaiter = this.waiters.shift();
    if (nextWaiter) {
      releasedSlot.busy = true;
      nextWaiter.resolve(releasedSlot);
    } else {
      releasedSlot.busy = false;
    }
  }
}

async function executeCommandWithRuntime(runtime, normalizedCommandParts, onNewLineOrOptions) {
  const options = normalizeExecuteOptions(onNewLineOrOptions);
  if (options.signal?.aborted) {
    return { lines: [], cancelled: true };
  }

  const capture = createOutputCapture(runtime, options);

  try {
    await new Promise((resolve, reject) => {
      try {
        runtime.main(normalizedCommandParts, (error) => {
          if (error) {
            reject(error);
          } else {
            resolve();
          }
        });
      } catch (error) {
        reject(error);
      }
    });
  } catch (error) {
    if (!capture.wasCancelled()) {
      throw error;
    }
  } finally {
    capture.restore();
  }

  return {
    lines: capture.lines,
    cancelled: capture.wasCancelled(),
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

export function createRuntimePool(options) {
  return new RuntimePool(options);
}
