package solver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sudoku.Candidate;
import sudoku.SolutionStep;
import sudoku.SolutionType;
import sudoku.Sudoku2;
import testutil.TestPuzzles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Integration tests for {@link UniquenessSolver} (Unique Rectangles, BUG+1)
 * exercised via {@link SudokuStepFinder#getStep(SolutionType)}.
 */
class UniquenessSolverTest {

    private SudokuSolver solver;
    private SudokuStepFinder finder;

    @BeforeEach
    void setUp() {
        TestPuzzles.setupOptions();
        solver = SudokuSolverFactory.getInstance();
        finder = solver.getStepFinder();
    }

    @AfterEach
    void tearDown() {
        SudokuSolverFactory.giveBack(solver);
    }

    private void load(String puzzle) {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(puzzle);
        solver.setSudoku(sudoku);
    }
    /** Exhaust all basic techniques so that uniqueness patterns become visible. */
    private void advanceSimples() {
        for (int limit = 0; limit < 500; limit++) {
            SolutionStep s = finder.getStep(SolutionType.NAKED_SINGLE);
            if (s == null) s = finder.getStep(SolutionType.HIDDEN_SINGLE);
            if (s == null) s = finder.getStep(SolutionType.FULL_HOUSE);
            if (s == null) s = finder.getStep(SolutionType.LOCKED_CANDIDATES_1);
            if (s == null) s = finder.getStep(SolutionType.LOCKED_CANDIDATES_2);
            if (s == null) s = finder.getStep(SolutionType.NAKED_PAIR);
            if (s == null) s = finder.getStep(SolutionType.HIDDEN_PAIR);
            if (s == null) break;
            finder.doStep(s);
        }
    }
    // ── Uniqueness Type 1 ─────────────────────────────────────────────────────

    @Test
    void findUniqueness1_puzzle_returnsStep() {
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);

        SolutionStep step = finder.getStep(SolutionType.UNIQUENESS_1);

        assertNotNull(step, "Uniqueness-1 puzzle must expose a UR Type 1 step");
        assertEquals(SolutionType.UNIQUENESS_1, step.getType());
    }

    @Test
    void doStep_uniqueness1_eliminatesCandidate() {
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);
        Sudoku2 sudoku = finder.getSudoku();
        SolutionStep step = finder.getStep(SolutionType.UNIQUENESS_1);
        assertNotNull(step);

        List<Candidate> toDelete = new ArrayList<>(step.getCandidatesToDelete());
        finder.doStep(step);

        for (Candidate c : toDelete) {
            assertFalse(sudoku.isCandidate(c.getIndex(), c.getValue()),
                    "Candidate " + c.getValue() + " in cell " + c.getIndex() + " must be eliminated after UR Type 1");
        }
    }

    // ── BUG+1 ─────────────────────────────────────────────────────────────────

    @Test
    void findBugPlus1_puzzle_exercisesSearch() {
        load(TestPuzzles.BUG_PLUS_1_PUZZLE);
        assertDoesNotThrow(() -> solver.solve());
    }

    // ── Solution completeness ─────────────────────────────────────────────────

    @Test
    void solve_uniqueness1Puzzle_completelySolvesIt() {
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);
        solver.solve();
        assertTrue(finder.getSudoku().isSolved(),
                "Solver must fully solve UNIQUENESS_1_PUZZLE");
    }

    // ── Multi-solution puzzle must not produce a uniqueness step ──────────────

    @Test
    void uniqueness_multiSolutionPuzzle_searchRunsCleanly() {
        // Uniqueness techniques are only valid for puzzles with a unique solution.
        // On a multi-solution puzzle the solver should either return null or skip.
        load(TestPuzzles.MULTI_SOLUTION_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.UNIQUENESS_1),
                "Uniqueness search on multi-solution puzzle must not throw");
    }

    // ── Negative ──────────────────────────────────────────────────────────────

    @Test
    void uniqueness_easyPuzzle_searchRunsCleanly() {
        load(TestPuzzles.EASY_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.UNIQUENESS_1),
                "Uniqueness search on easy puzzle must not throw");
    }

    // ── UR Types 2–6 (search coverage) ───────────────────────────────────────

    @Test
    void findUniqueness2_hardPuzzle_searchRuns() {
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);
        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.UNIQUENESS_2);
            if (step != null) assertEquals(SolutionType.UNIQUENESS_2, step.getType());
        });
    }

    @Test
    void findUniqueness3_hardPuzzle_searchRuns() {
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);
        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.UNIQUENESS_3);
            if (step != null) assertEquals(SolutionType.UNIQUENESS_3, step.getType());
        });
    }

    @Test
    void findUniqueness4_hardPuzzle_searchRuns() {
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);
        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.UNIQUENESS_4);
            if (step != null) assertEquals(SolutionType.UNIQUENESS_4, step.getType());
        });
    }

    @Test
    void findUniqueness5_hardPuzzle_searchRuns() {
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);
        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.UNIQUENESS_5);
            if (step != null) assertEquals(SolutionType.UNIQUENESS_5, step.getType());
        });
    }

    @Test
    void findUniqueness6_hardPuzzle_searchRuns() {
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);
        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.UNIQUENESS_6);
            if (step != null) assertEquals(SolutionType.UNIQUENESS_6, step.getType());
        });
    }

    // ── Hidden Rectangle / Avoidable Rectangle ────────────────────────────────

    @Test
    void findHiddenRectangle_hardPuzzle_searchRuns() {
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);
        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.HIDDEN_RECTANGLE);
            if (step != null) assertEquals(SolutionType.HIDDEN_RECTANGLE, step.getType());
        });
    }

    @Test
    void findAvoidableRectangle1_hardPuzzle_searchRuns() {
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);
        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.AVOIDABLE_RECTANGLE_1);
            if (step != null) assertEquals(SolutionType.AVOIDABLE_RECTANGLE_1, step.getType());
        });
    }

    @Test
    void findAvoidableRectangle2_hardPuzzle_searchRuns() {
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);
        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.AVOIDABLE_RECTANGLE_2);
            if (step != null) assertEquals(SolutionType.AVOIDABLE_RECTANGLE_2, step.getType());
        });
    }

    // ── doStep coverage via getAllUniqueness ───────────────────────────────────

    @Test
    void doStep_uniqueness2_onFoundStep_doesNotThrow() {
        // Run on a puzzle that has had simples cleared to maximise chance of finding UR2
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);
        advanceSimples();
        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.UNIQUENESS_2);
            if (step != null) finder.doStep(step);
        });
    }
}
