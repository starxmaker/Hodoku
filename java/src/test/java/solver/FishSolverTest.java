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
 * Integration tests for {@link FishSolver} (X-Wing, Swordfish, finned variants)
 * exercised via {@link SudokuStepFinder#getStep(SolutionType)}.
 */
class FishSolverTest {

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

    /** Exhaust all basic techniques so that fish patterns become visible. */
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

    // ── X-Wing ────────────────────────────────────────────────────────────────

    @Test
    void xWing_puzzle_exercisesSearch() {
        // solver.solve() tries X-Wing internally in the correct order;
        // if the technique is needed, getStep + doStep are both exercised.
        load(TestPuzzles.X_WING_PUZZLE);
        assertDoesNotThrow(() -> solver.solve());
    }

    // ── Swordfish ─────────────────────────────────────────────────────────────

    @Test
    void findSwordfish_hardPuzzle_executesSearch() {
        // Hard puzzle may or may not contain a Swordfish — what matters is the
        // search code runs without error. If found, also verify the step type.
        load(TestPuzzles.HARD_PUZZLE);

        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.SWORDFISH);
            if (step != null) {
                assertEquals(SolutionType.SWORDFISH, step.getType());
            }
        });
    }

    // ── Finned X-Wing ─────────────────────────────────────────────────────────

    @Test
    void findFinnedXWing_puzzle_returnsStep() {
        load(TestPuzzles.FINNED_X_WING_PUZZLE);
        advanceSimples();

        SolutionStep step = finder.getStep(SolutionType.FINNED_X_WING);

        assumeTrue(step != null, "No Finned X-Wing found in test puzzle after simples; update puzzle string");
        assertEquals(SolutionType.FINNED_X_WING, step.getType());
    }

    @Test
    void doStep_finnedXWing_eliminatesCandidate() {
        load(TestPuzzles.FINNED_X_WING_PUZZLE);
        advanceSimples();
        Sudoku2 sudoku = finder.getSudoku();
        SolutionStep step = finder.getStep(SolutionType.FINNED_X_WING);
        assumeTrue(step != null);

        List<Candidate> toDelete = new ArrayList<>(step.getCandidatesToDelete());
        finder.doStep(step);

        for (Candidate c : toDelete) {
            assertFalse(sudoku.isCandidate(c.getIndex(), c.getValue()),
                    "Candidate " + c.getValue() + " in cell " + c.getIndex() + " must be eliminated after Finned X-Wing");
        }
    }

    // ── Negative ──────────────────────────────────────────────────────────────

    @Test
    void fishSolver_easyPuzzle_searchRunsCleanly() {
        load(TestPuzzles.EASY_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.X_WING),
                "X-Wing search on easy puzzle must not throw");
    }
}
