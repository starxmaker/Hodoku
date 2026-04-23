# hodoku-core

Minimal JavaScript wrapper around HoDoKu's TeaVM build. 

For the moment, just score rating is exposed, but if you need access to the core app, you can directly communicate with it as you would do with Hodoku CLI.

## API

```js
import {
  executeCommand,
  rateSudoku,
  rateSudokus,
  rateSudokuWithMaxScore,
  rateSudokuWithDifficulty,
} from 'hodoku-core';

const helpLines = await executeCommand(['/h']);
console.log(helpLines);

const rating = await rateSudoku('53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79');
console.log(rating);

const ratings = await rateSudokus([
  '53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79',
  '.....6....59.....82....8....45........3........6..3.54...325..6..................',
]);

const cappedByScore = await rateSudokuWithMaxScore(
  '53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79',
  300,
);

const cappedByDifficulty = await rateSudokuWithDifficulty(
  '53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79',
  'Medium',
);
```

Returned shape:

```js
{
  level: 'Easy',
  score: 204,
  solved: true,
  requiresGuessing: false,
  requiresTemplates: false,
  gaveUp: false
}
```

Score-capped helpers add:

```js
{
  maxScore: 300,
  withinMaxScore: true
}
```

Difficulty-capped helpers add:

```js
{
  maxDifficulty: 'Medium',
  withinDifficultyCap: true
}
```

Available exports:

- `executeCommand(commandParts)`
- `rateSudoku(puzzle)`
- `rateSudokus(puzzles)`
- `rateSudokuWithMaxScore(puzzle, maxScore)`
- `rateSudokusWithMaxScore(puzzles, maxScore)`
- `rateSudokuWithDifficulty(puzzle, maxDifficulty)`
- `rateSudokusWithDifficulty(puzzles, maxDifficulty)`

`executeCommand(commandParts)` sends the raw command parts directly to the TeaVM HoDoKu core and returns the emitted output as an array of lines.

## Accepted Puzzle Input

`rateSudoku()` forwards the puzzle to HoDoKu's parser, so it accepts the same common formats used by the CLI, including:

- An 81-character grid using digits, `.` or `0`
- HoDoKu library format
- Other HoDoKu-supported single-puzzle text formats

## Packaging Workflow

The npm package is prepared but not published automatically.

From the repository root:

```sh
mvn -DskipTests package
cd npm/hodoku-core
npm pack --dry-run
```

`npm pack` triggers `prepack`, which copies the generated TeaVM bundle from `target/teavm-js/Hodoku-teavm.js` into the package as `Hodoku-teavm.cjs`.

## License

`hodoku-core` is distributed under `GPL-3.0-or-later`.

The package includes the full license text in `LICENSE` and is based on HoDoKu, which carries the following copyright notices:

- Copyright (C) 2019-20 PseudoFish
- Copyright (C) 2008-12 Bernhard Hobiger