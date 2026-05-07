import { generateSudokus, generateSudoku } from '../index.js';

const HODOKU_DIFFICULTIES = new Set(['Easy', 'Medium', 'Hard', 'Unfair', 'Extreme']);

describe('generation api', () => {
  test('streams generated sudokus and collects the same objects', async () => {
    const callbackSudokus = [];

    const generatedSudokus = await generateSudokus({ quantity: 2 }, (generatedSudoku) => {
      callbackSudokus.push(generatedSudoku);
    });

    expect(generatedSudokus).toEqual(callbackSudokus);
    expect(generatedSudokus).toHaveLength(2);

    for (const generatedSudoku of generatedSudokus) {
      expect(generatedSudoku.puzzle).toHaveLength(81);
      expect(generatedSudoku.puzzle).toMatch(/^[0-9.]{81}$/);
      expect(generatedSudoku.solution).toHaveLength(81);
      expect(generatedSudoku.solution).toMatch(/^[1-9]{81}$/);
      expect(HODOKU_DIFFICULTIES.has(generatedSudoku.difficulty)).toBe(true);
      expect(Number.isInteger(generatedSudoku.score)).toBe(true);
      expect(generatedSudoku.score).toBeGreaterThanOrEqual(0);
      expect(typeof generatedSudoku.givenUp).toBe('boolean');
      expect(typeof generatedSudoku.bruteForced).toBe('boolean');
      expect(generatedSudoku.unsolvable).toBe(false);
    }
  }, 120000);

  test('applies difficulty and score filters', async () => {
    const generatedSudoku = await generateSudoku({
      difficulty: 'Easy',
      minScore: 0,
      maxScore: 800,
    });

    expect(generatedSudoku).not.toBeNull();
    expect(generatedSudoku.difficulty).toBe('Easy');
    expect(generatedSudoku.score).toBeGreaterThanOrEqual(0);
    expect(generatedSudoku.score).toBeLessThanOrEqual(800);
  }, 120000);

  test('supports callback cancellation', async () => {
    const generatedSudokus = await generateSudokus({ quantity: 5 }, (_generatedSudoku, control) => {
      control.cancel();
    });

    expect(generatedSudokus).toHaveLength(1);
  }, 120000);

  test('runs until cancellation when quantity is omitted', async () => {
    let callbackCount = 0;

    const generatedSudokus = await generateSudokus({ difficulty: 'Easy' }, (_generatedSudoku, control) => {
      callbackCount += 1;

      if (callbackCount === 2) {
        control.cancel();
      }
    });

    expect(generatedSudokus).toHaveLength(2);
    expect(callbackCount).toBe(2);
    expect(generatedSudokus.every((generatedSudoku) => generatedSudoku.difficulty === 'Easy')).toBe(true);
  }, 120000);
});