const RUNTIME_BUNDLE_URL = new URL('./Hodoku-teavm.cjs', import.meta.url);
const HODOKU_DIFFICULTIES = new Set(['Easy', 'Medium', 'Hard', 'Unfair', 'Extreme']);
const HODOKU_TECHNIQUE_NAMES = [
  'Full House',
  'Hidden Single',
  'Hidden Pair',
  'Hidden Triple',
  'Hidden Quadruple',
  'Naked Single',
  'Naked Pair',
  'Naked Triple',
  'Naked Quadruple',
  'Locked Pair',
  'Locked Triple',
  'Locked Candidates',
  'Locked Candidates Type 1 (Pointing)',
  'Locked Candidates Type 2 (Claiming)',
  'Skyscraper',
  '2-String Kite',
  'Uniqueness Test 1',
  'Uniqueness Test 2',
  'Uniqueness Test 3',
  'Uniqueness Test 4',
  'Uniqueness Test 5',
  'Uniqueness Test 6',
  'Bivalue Universal Grave + 1',
  'XY-Wing',
  'XYZ-Wing',
  'W-Wing',
  'X-Chain',
  'XY-Chain',
  'Remote Pair',
  'Nice Loop/AIC',
  'Continuous Nice Loop',
  'Discontinuous Nice Loop',
  'X-Wing',
  'Swordfish',
  'Jellyfish',
  'Squirmbag',
  'Whale',
  'Leviathan',
  'Finned X-Wing',
  'Finned Swordfish',
  'Finned Jellyfish',
  'Finned Squirmbag',
  'Finned Whale',
  'Finned Leviathan',
  'Sashimi X-Wing',
  'Sashimi Swordfish',
  'Sashimi Jellyfish',
  'Sashimi Squirmbag',
  'Sashimi Whale',
  'Sashimi Leviathan',
  'Franken X-Wing',
  'Franken Swordfish',
  'Franken Jellyfish',
  'Franken Squirmbag',
  'Franken Whale',
  'Franken Leviathan',
  'Finned Franken X-Wing',
  'Finned Franken Swordfish',
  'Finned Franken Jellyfish',
  'Finned Franken Squirmbag',
  'Finned Franken Whale',
  'Finned Franken Leviathan',
  'Mutant X-Wing',
  'Mutant Swordfish',
  'Mutant Jellyfish',
  'Mutant Squirmbag',
  'Mutant Whale',
  'Mutant Leviathan',
  'Finned Mutant X-Wing',
  'Finned Mutant Swordfish',
  'Finned Mutant Jellyfish',
  'Finned Mutant Squirmbag',
  'Finned Mutant Whale',
  'Finned Mutant Leviathan',
  'Sue de Coq',
  'Almost Locked Set XZ-Rule',
  'Almost Locked Set XY-Wing',
  'Almost Locked Set XY-Chain',
  'Death Blossom',
  'Template Set',
  'Template Delete',
  'Forcing Chain',
  'Forcing Chain Contradiction',
  'Forcing Chain Verity',
  'Forcing Net',
  'Forcing Net Contradiction',
  'Forcing Net Verity',
  'Brute Force',
  'Incomplete Solution',
  'Give Up',
  'Grouped Nice Loop/AIC',
  'Grouped Continuous Nice Loop',
  'Grouped Discontinuous Nice Loop',
  'Empty Rectangle',
  'Hidden Rectangle',
  'Avoidable Rectangle Type 1',
  'Avoidable Rectangle Type 2',
  'AIC',
  'Grouped AIC',
  'Simple Colors',
  'Multi Colors',
  'Kraken Fish',
  'Turbot Fish',
  'Kraken Fish Type 1',
  'Kraken Fish Type 2',
  'Dual 2-String Kite',
  'Dual Empty Rectangle',
  'Simple Colors Trap',
  'Simple Colors Wrap',
  'Multi Colors 1',
  'Multi Colors 2',
];

export const HODOKU_TECHNIQUES = new Set(HODOKU_TECHNIQUE_NAMES);

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

  const {
    onNewLine,
    onRating,
    includeSolution = false,
    signal,
  } = onNewLineOrOptions;
  if (onNewLine !== undefined && typeof onNewLine !== 'function') {
    throw new TypeError('onNewLine must be a function');
  }

  if (onRating !== undefined && typeof onRating !== 'function') {
    throw new TypeError('onRating must be a function');
  }

  if (typeof includeSolution !== 'boolean') {
    throw new TypeError('includeSolution must be a boolean');
  }

  validateAbortSignal(signal);

  return { onNewLine, onRating, includeSolution, signal };
}

function validateAbortSignal(signal) {
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
}

function normalizePuzzles(puzzles) {
  if (!Array.isArray(puzzles)) {
    throw new TypeError('puzzles must be an array of strings');
  }

  if (puzzles.length === 0) {
    throw new TypeError('puzzles must not be empty');
  }

  return puzzles.map((puzzle) => normalizeCommandPart(puzzle));
}

function normalizeMaxScore(maxScore) {
  if (maxScore === undefined) {
    return undefined;
  }

  if (!Number.isInteger(maxScore) || maxScore < 0) {
    throw new TypeError('maxScore must be a non-negative integer');
  }

  return maxScore;
}

function normalizeOptionalNonNegativeInteger(value, name) {
  if (value === undefined) {
    return undefined;
  }

  if (!Number.isInteger(value) || value < 0) {
    throw new TypeError(`${name} must be a non-negative integer`);
  }

  return value;
}

function normalizeDifficulty(difficulty) {
  if (difficulty === undefined) {
    return undefined;
  }

  if (typeof difficulty !== 'string') {
    throw new TypeError('difficulty must be a HoDoKu difficulty string');
  }

  const normalizedDifficulty = difficulty.trim();
  if (!HODOKU_DIFFICULTIES.has(normalizedDifficulty)) {
    throw new TypeError('difficulty must be one of Easy, Medium, Hard, Unfair, Extreme');
  }

  return normalizedDifficulty;
}

function normalizeRateOptions(options) {
  if (options === null || typeof options !== 'object' || Array.isArray(options)) {
    throw new TypeError('rateSudokus options must be an object');
  }

  if (options.includePath !== undefined) {
    throw new TypeError('includePath is only supported by rateSudoku');
  }

  const {
    puzzles,
    includeSolution = false,
    maxScore,
    signal,
  } = options;

  if (typeof includeSolution !== 'boolean') {
    throw new TypeError('includeSolution must be a boolean');
  }

  validateAbortSignal(signal);

  return {
    puzzles: normalizePuzzles(puzzles),
    includeSolution,
    maxScore: normalizeMaxScore(maxScore),
    signal,
  };
}

function normalizeRateSudokuOptions(options) {
  if (options === null || typeof options !== 'object' || Array.isArray(options)) {
    throw new TypeError('rateSudoku options must be an object');
  }

  const {
    puzzle,
    includeSolution = false,
    includePath = false,
    maxScore,
    signal,
  } = options;

  if (typeof includeSolution !== 'boolean') {
    throw new TypeError('includeSolution must be a boolean');
  }

  if (typeof includePath !== 'boolean') {
    throw new TypeError('includePath must be a boolean');
  }

  validateAbortSignal(signal);

  return {
    puzzle: normalizeCommandPart(puzzle),
    includeSolution,
    includePath,
    maxScore: normalizeMaxScore(maxScore),
    signal,
  };
}

function normalizeGenerateOptions(options) {
  if (options === null || typeof options !== 'object' || Array.isArray(options)) {
    throw new TypeError('generateSudokus options must be an object');
  }

  const {
    quantity,
    minScore,
    maxScore,
    difficulty,
    signal,
  } = options;

  if (quantity !== undefined && (!Number.isInteger(quantity) || quantity < 1)) {
    throw new TypeError('quantity must be a positive integer');
  }

  validateAbortSignal(signal);

  const normalizedMinScore = normalizeOptionalNonNegativeInteger(minScore, 'minScore');
  const normalizedMaxScore = normalizeOptionalNonNegativeInteger(maxScore, 'maxScore');
  if (
    normalizedMinScore !== undefined
    && normalizedMaxScore !== undefined
    && normalizedMinScore > normalizedMaxScore
  ) {
    throw new TypeError('minScore must be less than or equal to maxScore');
  }

  return {
    quantity,
    minScore: normalizedMinScore,
    maxScore: normalizedMaxScore,
    difficulty: normalizeDifficulty(difficulty),
    signal,
  };
}

function normalizeGenerateCall(options, onGenerated) {
  const normalizedOptions = normalizeGenerateOptions(options);
  if (onGenerated !== undefined && typeof onGenerated !== 'function') {
    throw new TypeError('onGenerated must be a function');
  }

  return {
    ...normalizedOptions,
    onGenerated,
  };
}

function normalizeRateCall(options, onRating) {
  const normalizedOptions = normalizeRateOptions(options);
  if (onRating !== undefined && typeof onRating !== 'function') {
    throw new TypeError('onRating must be a function');
  }

  return {
    ...normalizedOptions,
    onRating,
  };
}

function buildRatingCommandParts(options) {
  const commandParts = ['/o', 'stdout'];

  if (options.maxScore !== undefined) {
    commandParts.push('/ms', String(options.maxScore));
  }

  return commandParts.concat(options.puzzles);
}

function buildSingleRatingCommandParts(options) {
  const commandParts = ['/o', 'stdout'];

  if (options.maxScore !== undefined) {
    commandParts.push('/ms', String(options.maxScore));
  }

  if (options.includePath) {
    commandParts.push('/vp');
  }

  commandParts.push(options.puzzle);
  return commandParts;
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
    controls,
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

function createRatingCapture(runtime, options, controls) {
  const { onRating, includeSolution } = options;
  if (!onRating) {
    return null;
  }

  const runtimeRatingStream = runtime?.TeaVMRatingStream;
  if (typeof runtimeRatingStream?.setHandler !== 'function') {
    throw new Error('TeaVMRatingStream.setHandler is unavailable in this bundle. Rebuild HoDoKu and refresh Hodoku-teavm.cjs.');
  }

  const originalHandler = typeof runtimeRatingStream.getHandler === 'function'
    ? runtimeRatingStream.getHandler()
    : null;
  const originalIncludeSolution = typeof runtimeRatingStream.isIncludeSolution === 'function'
    ? runtimeRatingStream.isIncludeSolution()
    : false;

  if (typeof runtimeRatingStream.setIncludeSolution === 'function') {
    runtimeRatingStream.setIncludeSolution(includeSolution);
  }

  runtimeRatingStream.setHandler((puzzle, puzzleNumber, difficulty, score, givenUp, bruteForced, unsolvable, solution) => {
    const rating = {
      puzzle: typeof puzzle === 'string' ? puzzle : String(puzzle),
      puzzleNumber: Number(puzzleNumber),
      difficulty: typeof difficulty === 'string' ? difficulty : String(difficulty),
      score: Number(score),
      givenUp: Boolean(givenUp),
      bruteForced: Boolean(bruteForced),
      unsolvable: Boolean(unsolvable),
    };

    if (typeof solution === 'string' && solution.length > 0) {
      rating.solution = solution;
    }

    const result = onRating(rating, controls);

    if (result === false) {
      controls.cancel();
    }
  });

  return {
    restore() {
      if (typeof runtimeRatingStream.setIncludeSolution === 'function') {
        runtimeRatingStream.setIncludeSolution(originalIncludeSolution);
      }

      if (originalHandler == null) {
        runtimeRatingStream.clearHandler();
      } else {
        runtimeRatingStream.setHandler(originalHandler);
      }
    },
  };
}

function createGenerationCapture(runtime, onGenerated, controls) {
  const runtimeGenerationStream = runtime?.TeaVMGenerationStream;
  if (typeof runtimeGenerationStream?.setHandler !== 'function') {
    throw new Error('TeaVMGenerationStream.setHandler is unavailable in this bundle. Rebuild HoDoKu and refresh Hodoku-teavm.cjs.');
  }

  const originalHandler = typeof runtimeGenerationStream.getHandler === 'function'
    ? runtimeGenerationStream.getHandler()
    : null;

  runtimeGenerationStream.setHandler((puzzle, difficulty, score, givenUp, bruteForced, unsolvable, solution) => {
    const generatedSudoku = {
      puzzle: typeof puzzle === 'string' ? puzzle : String(puzzle),
      difficulty: typeof difficulty === 'string' ? difficulty : String(difficulty),
      score: Number(score),
      givenUp: Boolean(givenUp),
      bruteForced: Boolean(bruteForced),
      unsolvable: Boolean(unsolvable),
    };

    if (typeof solution === 'string' && solution.length > 0) {
      generatedSudoku.solution = solution;
    }

    const result = onGenerated(generatedSudoku, controls);
    if (result === false) {
      controls.cancel();
    }
  });

  return {
    restore() {
      if (originalHandler == null) {
        runtimeGenerationStream.clearHandler();
      } else {
        runtimeGenerationStream.setHandler(originalHandler);
      }
    },
  };
}

async function executeCommandWithRuntime(runtime, normalizedCommandParts, onNewLineOrOptions) {
  const options = normalizeExecuteOptions(onNewLineOrOptions);
  if (options.signal?.aborted) {
    return { lines: [], cancelled: true };
  }

  const capture = createOutputCapture(runtime, options);
  const ratingCapture = createRatingCapture(runtime, options, capture.controls);

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
    if (ratingCapture) {
      ratingCapture.restore();
    }
    capture.restore();
  }

  return {
    lines: capture.lines,
    cancelled: capture.wasCancelled(),
  };
}

async function executeCommandRatingsWithRuntime(runtime, normalizedCommandParts, options) {
  const ratings = [];
  await executeCommandWithRuntime(runtime, normalizedCommandParts, {
    onRating(rating, controls) {
      ratings.push(rating);

      if (options.onRating) {
        return options.onRating(rating, controls);
      }

      return undefined;
    },
    includeSolution: options.includeSolution,
    signal: options.signal,
  });

  return ratings;
}

function parseSolutionPathStep(line, stepNumber) {
  const text = line.trim();
  const separatorIndex = text.indexOf(':');
  const technique = separatorIndex === -1 ? text : text.slice(0, separatorIndex).trim();
  const description = separatorIndex === -1 ? '' : text.slice(separatorIndex + 1).trim();

  return {
    stepNumber,
    technique,
    description,
  };
}

function isSolutionPathStep(line) {
  const text = line.trim();
  const separatorIndex = text.indexOf(':');
  if (separatorIndex === -1) {
    return false;
  }

  const technique = text.slice(0, separatorIndex).trim();
  return HODOKU_TECHNIQUES.has(technique);
}

async function executeSingleRatingWithRuntime(runtime, options) {
  const normalizedCommandParts = normalizeCommandParts(buildSingleRatingCommandParts(options));
  let rating = null;

  const result = await executeCommandWithRuntime(runtime, normalizedCommandParts, {
    onRating(emittedRating) {
      rating = emittedRating;
    },
    includeSolution: options.includeSolution,
    signal: options.signal,
  });

  if (rating == null) {
    return null;
  }

  if (options.includePath) {
    rating.steps = result.lines
      .filter((line) => line.startsWith('   ') && isSolutionPathStep(line))
      .map((line, index) => parseSolutionPathStep(line, index + 1));
  }

  return rating;
}

async function executeGenerateWithRuntime(runtime, options) {
  if (options.signal?.aborted) {
    return [];
  }

  const generatedSudokus = [];
  const capture = createOutputCapture(runtime, { signal: options.signal });
  const generationCapture = createGenerationCapture(runtime, (generatedSudoku, controls) => {
    generatedSudokus.push(generatedSudoku);

    if (options.onGenerated) {
      return options.onGenerated(generatedSudoku, controls);
    }

    return undefined;
  }, capture.controls);

  try {
    const runtimeGenerator = runtime?.TeaVMGenerator;
    if (typeof runtimeGenerator?.generateSudokus !== 'function') {
      throw new Error('TeaVMGenerator.generateSudokus is unavailable in this bundle. Rebuild HoDoKu and refresh Hodoku-teavm.cjs.');
    }

    try {
      runtimeGenerator.generateSudokus(
        options.quantity ?? -1,
        options.difficulty ?? null,
        options.minScore ?? -1,
        options.maxScore ?? -1,
      );
    } catch (error) {
      if (!capture.wasCancelled()) {
        throw error;
      }
    }
  } finally {
    generationCapture.restore();
    capture.restore();
  }

  return generatedSudokus;
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

async function withRuntime(action) {
  const runtimeSource = await loadRuntimeSource();
  const runtime = instantiateRuntime(runtimeSource);
  return action(runtime);
}

export async function generateSudokus(options, onGenerated) {
  const normalizedCall = normalizeGenerateCall(options, onGenerated);
  return withRuntime((runtime) => executeGenerateWithRuntime(runtime, normalizedCall));
}

export async function generateSudoku(options) {
  const newOptions = {
    ...options,
    quantity: 1,
  }
  const sudokus = await generateSudokus(newOptions);
  return sudokus.length > 0 ? sudokus[0] : null;
}

export async function rateSudokus(options, onRating) {
  const normalizedCall = normalizeRateCall(options, onRating);
  const commandParts = normalizeCommandParts(buildRatingCommandParts(normalizedCall));

  return withRuntime((runtime) => executeCommandRatingsWithRuntime(runtime, commandParts, {
    includeSolution: normalizedCall.includeSolution,
    onRating: normalizedCall.onRating,
    signal: normalizedCall.signal,
  }));
}

export async function rateSudoku(options) {
  const normalizedOptions = normalizeRateSudokuOptions(options);

  return withRuntime((runtime) => executeSingleRatingWithRuntime(runtime, normalizedOptions));
}

