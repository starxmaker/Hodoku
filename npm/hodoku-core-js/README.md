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
import { createRuntime, createRuntimePool } from 'hodoku-core-js';

const runtime = createRuntime(); // or createRuntimePool(10) for multi runtimes

// show commands
const helpLines = await runtime.executeCommand(['/h']);
console.log(helpLines);

// Multi sudoku rating
const batchLines = await runtime.executeCommand([
  '/o',
  'stdout',
  '53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79',
  '.....6....59.....82....8....45........3........6..3.54...325..6..................',
]);
console.log(batchLines);

// multi runtime
const parallelPool = createRuntimePool(2);
const [helpA, helpB] = await Promise.all([
  parallelPool.executeCommand(['/h']),
  parallelPool.executeCommand(['/h']),
]);
console.log(helpA[0], helpB[0]);
parallelPool.dispose();

// give up at a given score
const expertPuzzle = [ '.....5..44.17....2.2.1.4.8....36......7....5.......8..2....3.473..512....8.......']
await runtime.executeCommand(['/o', 'stdout', '/ms', '1000', ...expertPuzzle], (line) => console.log(line))
// output: .....5..44.17....2.2.1.4.8....36......7....5.......8..2....3.473..512....8....... #1 Extreme (1478) gu

// cancel execution at a given time
await runtime.executeCommand(['/o', 'stdout', '/ms', '1000', ...expertPuzzle], (line) => {
  if line === "specific line" return false
})
// output ["whatever", "lines", "before", "specific line"]
// You can also pass `{ signal }` to request cancellation via an `AbortSignal`. 
// Cancellation takes effect on the next emitted line.

// cleaning
runtime.dispose();
```

Available exports:

- `createRuntime()`: Single runtime.
- `createRuntimePool(size)`: creates a reusable pool of isolated TeaVM runtime instances. Each pool slot is locked while it is running a command.

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
