import { createRuntime } from '../index.js';
import * as fs from 'fs';
import * as path from 'path';

const readTestData = () => {
    const filename = "test-data.csv"
    const csv = fs.readFileSync(path.join(new URL("./", import.meta.url).pathname, filename), "utf8");
    return csv.split('\n')
        .slice(1)
        .map(line => line.trim())
        .filter(line => line.length > 0)
        .map(line => line.split(',')
            .map(cell => cell.trim()
            .replaceAll("\"", ""))
        )
        .map (a => ({
            puzzle: a[0],
            difficulty: a[1],
            score: a[2]
        })).reduce((acc, {puzzle, difficulty, score}) => {
            acc[puzzle] = {difficulty, score: Number(score)}
            return acc
        }, {})
            
}

const regex =
  /^([\.0-9]{81})\s+#\d+\s+(Easy|Medium|Hard|Unfair|Extreme)\s+\((\d+)\)$/;
  
export const map = (line) => {
  if (line.includes("gu")) {
    return null // score of the solution surpassed one constraint, so hodoku gave up
  }
  const match = line.trim().match(regex);
  if (match) {
    return {
      puzzle: match[1],
      difficulty: match[2],
      score: Number(match[3]),
    }
  }
  return null
}

const testData = readTestData()
const puzzles = Object.keys(testData)
const runtime = createRuntime()
const results = await runtime.executeCommand(['/o', 'stdout', ...puzzles], (line) => {
    console.log(line)
})
const mappedResults = results.map(map).filter(r => r !== null).reduce((acc, {puzzle, difficulty, score}) => {
    acc[puzzle] = {difficulty, score}
    return acc
}, {})

let failures = 0
for (let i = 0; i < puzzles.length; i++) {
    const expected = testData[puzzles[i]]
    const actual = mappedResults[puzzles[i]]
    if (!actual || !expected) {
        failures++
        console.error(`Missing data for puzzle ${puzzles[i]}`)
        continue
    }
    if (actual.difficulty !== expected.difficulty) {
        failures++
        console.error(`Difficulty mismatch for puzzle ${puzzles[i]}: expected ${expected.difficulty}, got ${actual.difficulty}`)
    }
    if (actual.score !== expected.score) {
        failures++
        console.error(`Score mismatch for puzzle ${puzzles[i]}: expected ${expected.score}, got ${actual.score}`)
    }
    console.log(`Test passed for puzzle ${puzzles[i]}: difficulty ${actual.difficulty}, score ${actual.score}`) 
}
runtime.dispose();
if (failures === 0) {
    console.log("All tests passed!")
    process.exit(0)
} else {
    console.error(`${failures} tests failed.`)
    process.exit(1)
}