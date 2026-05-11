import { HODOKU_TECHNIQUES, rateSudokus, rateSudoku, applyStep } from '../index.js';

describe('rating api', () => {
  const HODOKU_DIFFICULTIES = new Set(['Easy', 'Medium', 'Hard', 'Unfair', 'Extreme']);
  const rawExpectedRatings = [
      ["43...7.2..1.3.9.4...725..6.95....6...6.........2.............73.41.6.9..57.9.....","Easy",220],
      ["....3...9.6.75.........9.8...24.6...4...9..3....5..6.4.2..7.5..8........3..28....","Easy",452],
      [".9.5.1..8....4.....1.6.2.......2..9............9..547.52....6.1.....854.4.8.....7","Medium",618],
      ["1.97.5...7.2.......3..9..6........2........59...43......16..79..762......2..4...3","Medium",918],
      [".3.9....826.3......9182.5........761....3...9....9..5.7...6.3.....5.1...........6","Medium",308],
      ["..325.86.....9..3.856.734........7..9.2........8....4..2...65........98...4.3..2.","Extreme",1886],
      ["98..4.1.6..4..2...6....3.548.215.........4.......9....4.6.......9..6...1..52.....","Unfair",1458],
      [".549.....18....3...9..73....3...7.4......8...5.842.....7.3.2.98....8......35.41..","Hard",956],
      ["......5....5...8926....8.3..4.8......8.67....76.3.2..5.......7...2.67..4...5..9..","Unfair",1394],
      ["...8.2.6.7429......5.........9.8.4.........81...2.5...19.....5.4..79.6..6....1...","Hard",908]
    ]

  const EASY_PUZZLE = '003020600900305001001806400008102900700000008006708200002609500800203009005010300';
  const BRUTE_FORCE_PUZZLE = '100000002090400050006000700050903000000070000000850040700000600030009080002000001';
  const MULTI_SOLUTION_GIVEN_UP_PUZZLE = '2957438614318659..8761925433874592166123874955492167387635241899286713541549386..';
  const NO_SOLUTION_PUZZLE = '110000000000000000000000000000000000000000000000000000000000000000000000000000000';
  const EASY_PUZZLE_FIRST_PATH_STEPS = [
    {
      technique: 'Naked Single',
      notation: 'r5c6=4',
      actions: [{ type: 'set', row: 5, col: 6, value: 4 }],
    },
    {
      technique: 'Naked Single',
      notation: 'r9c4=4',
      actions: [{ type: 'set', row: 9, col: 4, value: 4 }],
    },
    {
      technique: 'Naked Single',
      notation: 'r5c7=1',
      actions: [{ type: 'set', row: 5, col: 7, value: 1 }],
    },
    {
      technique: 'Naked Single',
      notation: 'r5c3=9',
      actions: [{ type: 'set', row: 5, col: 3, value: 9 }],
    },
    {
      technique: 'Naked Single',
      notation: 'r9c6=7',
      actions: [{ type: 'set', row: 9, col: 6, value: 7 }],
    },
    {
      technique: 'Full House',
      notation: 'r1c6=1',
      actions: [{ type: 'set', row: 1, col: 6, value: 1 }],
    },
  ];
  const ACTIONS_PUZZLE = '...15........86.5...74..8.13..6.4..58.1..97....2.......9..38..........6.......3.9';
  const expectedRatings = rawExpectedRatings.map(([puzzle, difficulty, score]) => ({
      puzzle,
      difficulty,
      score,
      }));
  const SLOW_BRUTE_FORCE_TIMEOUT_MS = 360000;
  test('calls the handler and collects the same ratings', async () => {
    const sample = expectedRatings.slice(0, 3);
    const callbackRatings = [];

    const ratings = await rateSudokus(
      { puzzles: sample.map((rating) => rating.puzzle) },
      (rating) => {
        callbackRatings.push(rating);
      },
    );

    expect(ratings).toEqual(callbackRatings);

    for (let index = 0; index < sample.length; index += 1) {
      expect(ratings[index]).toMatchObject({
        puzzle: sample[index].puzzle,
        puzzleNumber: index + 1,
        difficulty: sample[index].difficulty,
        score: sample[index].score,
        givenUp: false,
        bruteForced: false,
        unsolvable: false,
      });
    }
  });

  test('supports passing the handler as the second argument', async () => {
    const sample = expectedRatings.slice(0, 2);
    const callbackRatings = [];

    const ratings = await rateSudokus({ puzzles: sample.map((rating) => rating.puzzle) }, (rating) => {
      callbackRatings.push(rating);
    });

    expect(ratings).toEqual(callbackRatings);
  });

  test('returns ratings without a callback', async () => {
    const sample = expectedRatings.slice(0, 2);
    const ratings = await rateSudokus({ puzzles: sample.map((rating) => rating.puzzle) });

    expect(ratings).toHaveLength(2);
    expect(ratings[0]).toMatchObject({
      puzzle: sample[0].puzzle,
      puzzleNumber: 1,
      difficulty: sample[0].difficulty,
      score: sample[0].score,
      givenUp: false,
      bruteForced: false,
      unsolvable: false,
    });
    expect(ratings[1]).toMatchObject({
      puzzle: sample[1].puzzle,
      puzzleNumber: 2,
      difficulty: sample[1].difficulty,
      score: sample[1].score,
      givenUp: false,
      bruteForced: false,
      unsolvable: false,
    });
  });

  // skipping as it takes a long time
  test.skip('marks brute-forced ratings', async () => {
    const rating = await rateSudoku({ puzzle: BRUTE_FORCE_PUZZLE });

    expect(rating.bruteForced).toBe(true);
    expect(rating.givenUp).toBe(false);
    expect(rating.unsolvable).toBe(false);
  }, SLOW_BRUTE_FORCE_TIMEOUT_MS);

  test('marks given-up ratings when maxScore aborts the solve', async () => {
    const rating = await rateSudoku({ puzzle: EASY_PUZZLE, maxScore: 20 });

    expect(rating.givenUp).toBe(true);
    expect(rating.bruteForced).toBe(false);
    expect(rating.unsolvable).toBe(false);
  }, 60000);

  test('marks given-up ratings for the known multi-solution puzzle', async () => {
    const rating = await rateSudoku({ puzzle: MULTI_SOLUTION_GIVEN_UP_PUZZLE });

    expect(rating).toMatchObject({
      puzzle: MULTI_SOLUTION_GIVEN_UP_PUZZLE,
      difficulty: 'Extreme',
      score: 20000,
      givenUp: true,
      bruteForced: false,
      unsolvable: false,
    });
  }, 60000);

  test('marks contradictory puzzles as unsolvable', async () => {
    const rating = await rateSudoku({ puzzle: NO_SOLUTION_PUZZLE });

    expect(rating).toMatchObject({
      puzzle: NO_SOLUTION_PUZZLE,
      difficulty: 'Extreme',
      score: 0,
      givenUp: true,
      bruteForced: false,
      unsolvable: true,
    });
  }, 60000);

  test('includes the solution when requested', async () => {
    const rating = await rateSudoku({
      puzzle: expectedRatings[0].puzzle,
      includeSolution: true,
    });

    expect(rating.puzzle).toBe(expectedRatings[0].puzzle);
    expect(typeof rating.solution).toBe('string');
    expect(rating.solution).toHaveLength(81);
    expect(rating.solution).toMatch(/^[1-9]{81}$/);
  });

  test('includes the verbose solution path for single sudoku ratings', async () => {
    const rating = await rateSudoku({
      puzzle: EASY_PUZZLE,
      includePath: true,
    });

    expect(Array.isArray(rating.steps)).toBe(true);
    expect(rating.steps.length).toBeGreaterThan(0);
    expect(rating.steps[0]).toMatchObject({
      stepNumber: 1,
    });
    expect(rating.steps).toHaveLength(49);
    expect(rating.steps.slice(0, EASY_PUZZLE_FIRST_PATH_STEPS.length).map((step) => ({
      technique: step.technique,
      notation: step.notation,
      actions: step.actions,
    }))).toEqual(EASY_PUZZLE_FIRST_PATH_STEPS);
    expect(rating.steps.some((step) => HODOKU_DIFFICULTIES.has(step.technique))).toBe(false);

    for (let index = 0; index < rating.steps.length; index += 1) {
      const step = rating.steps[index];
      expect(step.stepNumber).toBe(index + 1);
      expect(typeof step.technique).toBe('string');
      expect(step.technique.length).toBeGreaterThan(0);
      expect(HODOKU_TECHNIQUES.has(step.technique)).toBe(true);
      expect(typeof step.notation).toBe('string');
      expect(Array.isArray(step.actions)).toBe(true);
      for (const action of step.actions) {
        expect(action.type === 'set' || action.type === 'eliminate').toBe(true);
        expect(Number.isInteger(action.row)).toBe(true);
        expect(Number.isInteger(action.col)).toBe(true);
        expect(Number.isInteger(action.value)).toBe(true);
      }
    }
  }, 60000);

  test('includes eliminate actions for non-single path steps', async () => {
    const rating = await rateSudoku({
      puzzle: ACTIONS_PUZZLE,
      includePath: true,
    });

    const eliminationStep = rating.steps.find((step) => step.actions.some((action) => action.type === 'eliminate'));

    expect(eliminationStep).toBeDefined();
    expect(eliminationStep.actions.every((action) => action.type === 'eliminate')).toBe(true);
  }, 60000);

  test('exports the HoDoKu technique set', () => {
    expect(HODOKU_TECHNIQUES.has('Naked Single')).toBe(true);
    expect(HODOKU_TECHNIQUES.has('Kraken Fish Type 2')).toBe(true);
    expect(HODOKU_TECHNIQUES.has('Easy')).toBe(false);
  });

  test('rejects includePath in batch ratings', async () => {
    await expect(rateSudokus({ puzzles: [EASY_PUZZLE], includePath: true })).rejects.toThrow(
      'includePath is only supported by rateSudoku',
    );
  });

  test.each(expectedRatings)('parity check: puzzle $puzzle, difficulty $difficulty, score $score', async ({ puzzle, difficulty, score }) => {
    const rating = await rateSudoku({ puzzle });
    expect(rating).not.toBeNull();
    expect(rating).toMatchObject({
      puzzle: puzzle,
      difficulty: difficulty,
      score: score,
      unsolvable: false,
    });
  }, 10_000);
});


describe('candidate inline format', () => {
  const INPUT_PUZZLE =
    ".84923.1...3.5.8.2..2.8...3318.9..2.49.3.2.812.7418.3.83.2.{123456789}1.6.41.3927872.8.13..";
  const OUTPUT_PUZZLE =
    ".84923.1...3.5.8.2..2.8...3318.9..2.49.3.2.812.7418.3.83.2.{12356789}1.6.41.3927872.8.13..";

  const ELIMINATION_STEP = {
      stepNumber: 1,
      technique: 'Locked Candidates Type 1 (Pointing)',
      notation: '4  in b2 => r7c6<>4',
      actions: [
        { type: 'eliminate', row: 7, col: 6, value: 4 }],
  };
  test('Locked Candidates Type 1 (Pointing) appears when candidate 4 is present', async () => {
    const rating = await rateSudoku({
      puzzle: INPUT_PUZZLE,
      includePath: true,
    });

    expect(rating).not.toBeNull();
    const firstStep = rating.steps[0];
    expect(firstStep).toMatchObject({
      stepNumber: 1,
      technique: 'Locked Candidates Type 1 (Pointing)',
      notation: '4  in b2 => r7c6<>4',
      actions: [
        { type: 'eliminate', row: 7, col: 6, value: 4 }],
    });
  });

  test('Locked Candidates Type 1 (Pointing) not needed when candidate 4 already removed', async () => {
    const rating = await rateSudoku({
      puzzle: OUTPUT_PUZZLE,
      includePath: true,
    });

    expect(rating).not.toBeNull();
    const firstStep = rating.steps[0];
    expect(firstStep).not.toMatchObject(ELIMINATION_STEP);
    expect(firstStep.stepNumber).toBe(1);
  });

  test('Applying step should remove candidate', async () => {
    const newPuzzle = applyStep(INPUT_PUZZLE, ELIMINATION_STEP);
    expect(newPuzzle).toBe(OUTPUT_PUZZLE);
  });

  test('No-OP if the candidate has already been removed', async () => {
    const newPuzzle = applyStep(OUTPUT_PUZZLE, ELIMINATION_STEP);
    expect(newPuzzle).toBe(OUTPUT_PUZZLE);
  });
  
});
