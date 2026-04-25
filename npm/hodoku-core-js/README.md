# hodoku-core-js

Minimal JavaScript wrapper around HoDoKu's TeaVM build.

## Limitations

- Pool slots isolate runtime state, but they do not guarantee CPU-parallel execution in every host.
- Only default console profile is allowed
- Slower than Java version (but faster than hodoku-difficulty-rating-ts).

## API

```js
import { createRuntimePool } from 'hodoku-core-js';

const pool = createRuntimePool();

const helpLines = await pool.executeCommand(['/h']);
console.log(helpLines);

const batchLines = await pool.executeCommand([
  '/o',
  'stdout',
  '53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79',
  '.....6....59.....82....8....45........3........6..3.54...325..6..................',
]);
console.log(batchLines);

await pool.executeCommand(['/h'], (line) => {
  console.log('HoDoKu:', line);
});

const firstPuzzleOnly = await pool.executeCommand(
  [
    '/o',
    'stdout',
    '53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79',
    '.....6....59.....82....8....45........3........6..3.54...325..6..................',
  ],
  (line) => {
    if (line.includes('#1 ')) {
      return false;
    }
  },
);
console.log(firstPuzzleOnly);

const parallelPool = createRuntimePool({ size: 2 });
const [helpA, helpB] = await Promise.all([
  parallelPool.executeCommand(['/h']),
  parallelPool.executeCommand(['/h']),
]);
console.log(helpA[0], helpB[0]);
parallelPool.dispose();

pool.dispose();
```

Available exports:

- `createRuntimePool({ size })`

`createRuntimePool({ size })` creates a reusable pool of isolated TeaVM runtime instances. Each pool slot is locked while it is running a command.

Call `pool.executeCommand(commandParts, onNewLineOrOptions)` to send raw command parts directly to the TeaVM HoDoKu core and receive the emitted output as an array of lines.

Pass the optional second argument either as a listener function or as an options object.

If the listener returns `false`, HoDoKu cancels the current command and `pool.executeCommand()` resolves with the lines collected so far.

You can also pass `{ signal }` to request cancellation via an `AbortSignal`. Cancellation takes effect on the next emitted line.

## Accepted Puzzle Input

When a command includes puzzle text, HoDoKu parses the puzzle input using the same formats accepted by the CLI, including:

- An 81-character grid using digits, `.` or `0`
- HoDoKu library format
- Other HoDoKu-supported single-puzzle text formats

If more than one positional puzzle is passed, HoDoKu applies the grouped inline batch-solve mode automatically.

## Packaging Workflow

From the repository root:

```sh
mvn clean package
cd npm_recovered/hodoku-core-js
npm pack --dry-run
```

`npm pack` triggers `prepack`, which copies the generated TeaVM bundle from `target/teavm-js/Hodoku-teavm.js` into the package as `Hodoku-teavm.cjs`.

## License

`hodoku-core-js` is distributed under `GPL-3.0-or-later`.

The package includes the full license text in `LICENSE` and is based on HoDoKu, which carries the following copyright notices:

- Copyright (C) 2019-20 PseudoFish
- Copyright (C) 2008-12 Bernhard Hobiger
