const RATING_MARKER = 'HODOKU_RATING ';
const RATINGS_MARKER = 'HODOKU_RATINGS ';
const DIFFICULTY_LEVELS = ['Incomplete', 'Easy', 'Medium', 'Hard', 'Unfair', 'Extreme'];
const DIFFICULTY_RANK = new Map(DIFFICULTY_LEVELS.map((level, index) => [level.toLowerCase(), index]));

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

function parseRating(puzzle, lines) {
  const ratingLine = lines.find((line) => line.startsWith(RATING_MARKER));
  if (!ratingLine) {
    const output = lines.join('\n').trim();
    throw new Error(output ? `HoDoKu rating output not found for puzzle "${puzzle}":\n${output}` : `HoDoKu rating output not found for puzzle "${puzzle}".`);
  }

  let rating = JSON.parse(ratingLine.slice(RATING_MARKER.length));
  if (rating.error) {
    const error = new Error(`HoDoKu rating failed for puzzle "${puzzle}": ${rating.error}`);
    error.code = rating.error;
    throw error;
  }
  rating.puzzle = puzzle;
  return rating;
}

function parseRatings(puzzles, lines) {
  const ratingLine = lines.find((line) => line.startsWith(RATINGS_MARKER));
  if (!ratingLine) {
    const output = lines.join('\n').trim();
    throw new Error(output ? `HoDoKu batch rating output not found:\n${output}` : 'HoDoKu batch rating output not found.');
  }

  const ratings = JSON.parse(ratingLine.slice(RATINGS_MARKER.length));
  if (!Array.isArray(ratings)) {
    throw new Error('HoDoKu batch rating output is invalid.');
  }

  if (ratings.length !== puzzles.length) {
    throw new Error(`HoDoKu batch rating count mismatch: expected ${puzzles.length}, got ${ratings.length}.`);
  }

  return ratings.map((rating, index) => {
    if (rating.error) {
      const error = new Error(`HoDoKu rating failed for puzzle "${puzzles[index]}": ${rating.error}`);
      error.code = rating.error;
      throw error;
    }

    return {
      ...rating,
      puzzle: puzzles[index],
    };
  });
}

function normalizePuzzle(puzzle) {
  if (typeof puzzle !== 'string') {
    throw new TypeError('puzzle must be a string');
  }

  const normalizedPuzzle = puzzle.trim();
  if (normalizedPuzzle.length === 0) {
    throw new TypeError('puzzle must not be empty');
  }

  return normalizedPuzzle;
}

function normalizePuzzles(puzzles) {
  if (!Array.isArray(puzzles)) {
    throw new TypeError('puzzles must be an array of strings');
  }

  return puzzles.map((puzzle) => normalizePuzzle(puzzle));
}

function normalizeCommandParts(commandParts) {
  if (!Array.isArray(commandParts)) {
    throw new TypeError('commandParts must be an array of strings');
  }

  return commandParts.map((part) => normalizePuzzle(part));
}

function normalizeScore(value, optionName) {
  if (typeof value !== 'number' || !Number.isFinite(value)) {
    throw new TypeError(`${optionName} must be a finite number`);
  }

  return value;
}

function normalizeMaxScore(maxScore) {
  return normalizeScore(maxScore, 'maxScore');
}

function normalizeDifficulty(difficulty, optionName) {
  if (typeof difficulty !== 'string') {
    throw new TypeError(`${optionName} must be a string`);
  }

  const normalizedDifficulty = difficulty.trim().toLowerCase();
  if (normalizedDifficulty.length === 0) {
    throw new TypeError(`${optionName} must not be empty`);
  }

  const rank = DIFFICULTY_RANK.get(normalizedDifficulty);
  if (rank === undefined) {
    throw new RangeError(`${optionName} must be one of: ${DIFFICULTY_LEVELS.join(', ')}`);
  }

  return DIFFICULTY_LEVELS[rank];
}

function normalizeDifficultyCap(maxDifficulty) {
  return normalizeDifficulty(maxDifficulty, 'maxDifficulty');
}

function normalizeMatchConstraints(constraints) {
  if (constraints === null || typeof constraints !== 'object' || Array.isArray(constraints)) {
    throw new TypeError('constraints must be an object');
  }

  const normalizedConstraints = {};

  if (constraints.difficulty !== undefined) {
    normalizedConstraints.difficulty = normalizeDifficulty(constraints.difficulty, 'difficulty');
  }

  if (constraints.minScore !== undefined) {
    normalizedConstraints.minScore = normalizeScore(constraints.minScore, 'minScore');
  }

  if (constraints.maxScore !== undefined) {
    normalizedConstraints.maxScore = normalizeScore(constraints.maxScore, 'maxScore');
  }

  if (
    normalizedConstraints.difficulty === undefined
    && normalizedConstraints.minScore === undefined
    && normalizedConstraints.maxScore === undefined
  ) {
    throw new TypeError('constraints must include at least one of: difficulty, minScore, maxScore');
  }

  if (
    normalizedConstraints.minScore !== undefined
    && normalizedConstraints.maxScore !== undefined
    && normalizedConstraints.minScore > normalizedConstraints.maxScore
  ) {
    throw new RangeError('minScore must be less than or equal to maxScore');
  }

  return normalizedConstraints;
}

function matchesRatingConstraints(rating, constraints) {
  if (constraints.difficulty !== undefined && rating.level !== constraints.difficulty) {
    return false;
  }

  if (constraints.minScore !== undefined && rating.score < constraints.minScore) {
    return false;
  }

  if (constraints.maxScore !== undefined && rating.score > constraints.maxScore) {
    return false;
  }

  return true;
}

function addMaxScoreCap(rating, maxScore) {
  const normalizedMaxScore = normalizeMaxScore(maxScore);
  return {
    ...rating,
    maxScore: normalizedMaxScore,
    withinMaxScore: rating.score <= normalizedMaxScore,
  };
}

function addDifficultyCap(rating, maxDifficulty) {
  const normalizedDifficulty = normalizeDifficultyCap(maxDifficulty);
  const ratingRank = DIFFICULTY_RANK.get(String(rating.level).toLowerCase());

  if (ratingRank === undefined) {
    throw new RangeError(`Unknown HoDoKu difficulty level: ${rating.level}`);
  }

  return {
    ...rating,
    maxDifficulty: normalizedDifficulty,
    withinDifficultyCap: ratingRank <= DIFFICULTY_RANK.get(normalizedDifficulty.toLowerCase()),
  };
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

export async function rateSudoku(puzzle) {
  const normalizedPuzzle = normalizePuzzle(puzzle);

  return parseRating(normalizedPuzzle, await executeCommand(['/rate', normalizedPuzzle]));
}

export async function rateSudokus(puzzles) {
  const normalizedPuzzles = normalizePuzzles(puzzles);

  return parseRatings(normalizedPuzzles, await executeCommand(['/rate-many', normalizedPuzzles.join('|')]));
}

export async function rateSudokusUntilMatch(puzzles, constraints) {
  const normalizedPuzzles = normalizePuzzles(puzzles);
  const normalizedConstraints = normalizeMatchConstraints(constraints);

  for (const puzzle of normalizedPuzzles) {
    const rating = await rateSudoku(puzzle);
    if (matchesRatingConstraints(rating, normalizedConstraints)) {
      return rating;
    }
  }

  return null;
}

export async function rateSudokuWithMaxScore(puzzle, maxScore) {
  return addMaxScoreCap(await rateSudoku(puzzle), maxScore);
}

export async function rateSudokusWithMaxScore(puzzles, maxScore) {
  const ratings = await rateSudokus(puzzles);
  return ratings.map((rating) => addMaxScoreCap(rating, maxScore));
}

export async function rateSudokuWithDifficulty(puzzle, maxDifficulty) {
  return addDifficultyCap(await rateSudoku(puzzle), maxDifficulty);
}

export async function rateSudokusWithDifficulty(puzzles, maxDifficulty) {
  const ratings = await rateSudokus(puzzles);
  return ratings.map((rating) => addDifficultyCap(rating, maxDifficulty));
}

export default rateSudoku;
