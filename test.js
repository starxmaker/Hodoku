import { rateSudoku } from './index.js'

const sudoku = "63.8.2......7.....81...95..9....38..2..5....7.5.2....93.......2.9...6...1.....9.."

const rating = await rateSudoku({
    puzzle: sudoku,
    includePath: true,
})

console.log(rating.steps);