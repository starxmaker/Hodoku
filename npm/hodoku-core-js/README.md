# hodoku-core-js

Minimal JavaScript wrapper around HoDoKu's TeaVM build.

## Limitations


## API

```js
import { executeCommand } from 'hodoku-core-js';

const helpLines = await executeCommand(['/h']);
console.log(helpLines);

const batchLines = await executeCommand([
  '/o',
  'stdout',
  '53..7....6..195....98....6.8...6...34..8.3..17...2...6.6....28....419..5....8..79',
  '.....6....59.....82....8....45........3........6..3.54...325..6..................',
]);
console.log(batchLines);

await executeCommand(['/h'], (line) => {
  console.log('HoDoKu:', line);
});
```

Available exports:

- `executeCommand(commandParts)`

`executeCommand(commandParts)` sends the raw command parts directly to the TeaVM HoDoKu core and returns the emitted output as an array of lines.

Pass an optional second argument to receive each emitted output line as it arrives.

## Accepted Puzzle Input

When a command includes puzzle text, HoDoKu parses the puzzle input using the same formats accepted by the CLI, including:

- An 81-character grid using digits, `.` or `0`
- HoDoKu library format
- Other HoDoKu-supported single-puzzle text formats

If more than one positional puzzle is passed, HoDoKu applies the grouped inline batch-solve mode automatically.

## Packaging Workflow

The npm package is prepared but not published automatically.

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
