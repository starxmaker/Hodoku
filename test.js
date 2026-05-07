import { generateSudokus } from "./index.js";

console.log("Starting at " + new Date().toISOString());
const sudokus = []
await generateSudokus({
    difficulty: 'Extreme',
    minScore: 10_000
}, (puzzle, control) => {
    console.log("Generated sudoku with score " + puzzle.score + " at " + new Date().toISOString());
    sudokus.push(puzzle);
    if (sudokus.length >= 5) {
        control.cancel();
    }
});
console.log("Finished at " + new Date().toISOString());
console.log("Generated sudokus:");
console.log(sudokus);