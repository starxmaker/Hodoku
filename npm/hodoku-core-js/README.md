# hodoku-core-js

Minimal JavaScript wrapper around HoDoKu's TeaVM build.

Slower than Java version (but faster than hodoku-difficulty-rating-ts). Use it if you really need to use it in a node only environment or in the browser.

Install with: 

```bash
npm install hodoku-core-js
```

## Additional modifications

- Code changes to make it compatible with TeaVM
  - Only default console profile is allowed (there is an issue loading XML config)
- Sudoku input now is vararg. It means you can pass more than one sudoku to be rated (instead of having to use a file for that with /bs)
- Console printing is captured using a specific listener instead of console.log
- Ability to cancel execution via command listener
- `/ms (number)`, to given up on a sudoku rating after a specific score has been surpassed.

## API

```js
import { rateSudokus } from 'hodoku-core-js';

const puzzles = [
  '53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79',
  '.....6....59.....82....8....45........3........6..3.54...325..6..................',
];

const ratings = await rateSudokus({ puzzles });
console.log(ratings);

await rateSudokus({ puzzles, maxScore: 1000 }, (rating, control) => {
  console.log(rating);

  if (rating.score > 900) {
    control.cancel();
  }
});

const withSolution = await rateSudokus({
  puzzles: [puzzles[0]],
  includeSolution: true,
  maxScore: 1000,
});
console.log(withSolution[0].solution);
```

Available exports:

- `rateSudokus(options, onRating?)`: rates `options.puzzles`, optionally calls `onRating` for each emitted rating, and resolves with the collected ratings.

Each emitted rating includes `givenUp`, `bruteForced`, and `unsolvable` booleans in addition to `puzzle`, `puzzleNumber`, `difficulty`, `score`, and optional `solution`.

Each call creates a fresh isolated TeaVM runtime. That keeps handler state local to the call and avoids cross-request interference when callers run ratings concurrently.

## Accepted Puzzle Input

When a rating call includes puzzle text, HoDoKu parses the puzzle input using the same formats accepted by the CLI, including:

- An 81-character grid using digits, `.` or `0`
- HoDoKu library format
- Other HoDoKu-supported single-puzzle text formats

If `options.puzzles` contains more than one puzzle, HoDoKu applies the grouped inline batch-solve mode automatically.

## Packaging Workflow

From the repository root:

```sh
mvn clean package
cd npm/hodoku-core-js
npm test
npm run test:full
npm pack --dry-run
```

`npm test` runs the fast default Jest suite: callback coverage, non-callback coverage, given-up coverage, solution coverage, and a tiny essential parity sample. `npm run test:full` enables the slower brute-force coverage and the full-dataset parity case, and can take several minutes. `npm pack` triggers `prepack`, which copies the generated TeaVM bundle from `target/teavm-js/Hodoku-teavm.js` into the package as `Hodoku-teavm.cjs` and runs the default suite.

## License

`hodoku-core-js` is distributed under `GPL-3.0-or-later`.

The package includes the full license text in `LICENSE` and is based on HoDoKu, which carries the following copyright notices:

- Copyright (C) 2019-20 PseudoFish
- Copyright (C) 2008-12 Bernhard Hobiger
