const RATING_MARKER = 'HODOKU_RATING ';
const DIFFICULTY_LEVELS = ['Incomplete', 'Easy', 'Medium', 'Hard', 'Unfair', 'Extreme'];
const DIFFICULTY_RANK = new Map(DIFFICULTY_LEVELS.map((level, index) => [level.toLowerCase(), index]));

let runtimePromise;

async function loadRuntime() {
  if (!runtimePromise) {
    runtimePromise = import('./Hodoku-teavm.cjs').then((mod) => mod.default ?? mod);
  }

  return runtimePromise;
}

function toLogLine(args) {
  return args
    .map((arg) => {
      if (typeof arg === 'string') {
        return arg;
      }
      try {
        return JSON.stringify(arg);
      } catch (error) {
        return String(arg);
      }
    })
    .join(' ');
}

function createOutputCapture() {
  const lines = [];
  const originalConsole = {
    log: console.log,
    info: console.info,
    warn: console.warn,
    error: console.error,
  };

  const capture = (...args) => {
    lines.push(toLogLine(args));
  };

  console.log = capture;
  console.info = capture;
  console.warn = capture;
  console.error = capture;

  return {
    lines,
    restore() {
      console.log = originalConsole.log;
      console.info = originalConsole.info;
      console.warn = originalConsole.warn;
      console.error = originalConsole.error;
    },
  };
}

function parseRating(lines) {
  const ratingLine = lines.find((line) => line.startsWith(RATING_MARKER));
  if (!ratingLine) {
    const output = lines.join('\n').trim();
    throw new Error(output ? `HoDoKu rating output not found:\n${output}` : 'HoDoKu rating output not found.');
  }

  const rating = JSON.parse(ratingLine.slice(RATING_MARKER.length));
  if (rating.error) {
    const error = new Error(`HoDoKu rating failed: ${rating.error}`);
    error.code = rating.error;
    throw error;
  }

  return rating;
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

function normalizeMaxScore(maxScore) {
  if (typeof maxScore !== 'number' || !Number.isFinite(maxScore)) {
    throw new TypeError('maxScore must be a finite number');
  }

  return maxScore;
}

function normalizeDifficultyCap(maxDifficulty) {
  if (typeof maxDifficulty !== 'string') {
    throw new TypeError('maxDifficulty must be a string');
  }

  const normalizedDifficulty = maxDifficulty.trim().toLowerCase();
  if (normalizedDifficulty.length === 0) {
    throw new TypeError('maxDifficulty must not be empty');
  }

  const rank = DIFFICULTY_RANK.get(normalizedDifficulty);
  if (rank === undefined) {
    throw new RangeError(`maxDifficulty must be one of: ${DIFFICULTY_LEVELS.join(', ')}`);
  }

  return DIFFICULTY_LEVELS[rank];
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

export async function executeCommand(commandParts) {
  const normalizedCommandParts = normalizeCommandParts(commandParts);

  const runtime = await loadRuntime();
  const capture = createOutputCapture();

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

  return parseRating(await executeCommand(['/rate', normalizedPuzzle]));
}

export async function rateSudokus(puzzles) {
  const normalizedPuzzles = normalizePuzzles(puzzles);
  const ratings = [];

  for (const puzzle of normalizedPuzzles) {
    ratings.push(await rateSudoku(puzzle));
  }

  return ratings;
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
