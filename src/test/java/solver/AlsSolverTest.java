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

/**
 * Integration tests for {@link AlsSolver} (ALS-XZ, ALS-XY-Wing, Death Blossom)
 * exercised via {@link SudokuStepFinder#getStep(SolutionType)}.
 */
class AlsSolverTest {

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

    // ── ALS-XZ ────────────────────────────────────────────────────────────────

    @Test
    void findAlsXz_puzzle_returnsStep() {
        load(TestPuzzles.ALS_XZ_PUZZLE);

        SolutionStep step = finder.getStep(SolutionType.ALS_XZ);

        assertNotNull(step, "ALS-XZ puzzle must expose an ALS-XZ step");
        assertEquals(SolutionType.ALS_XZ, step.getType());
    }

    @Test
    void doStep_alsXz_eliminatesCandidate() {
        load(TestPuzzles.ALS_XZ_PUZZLE);
        Sudoku2 sudoku = finder.getSudoku();
        SolutionStep step = finder.getStep(SolutionType.ALS_XZ);
        assertNotNull(step);

        List<Candidate> toDelete = new ArrayList<>(step.getCandidatesToDelete());
        finder.doStep(step);

        for (Candidate c : toDelete) {
            assertFalse(sudoku.isCandidate(c.getIndex(), c.getValue()),
                    "Candidate " + c.getValue() + " in cell " + c.getIndex() + " must be eliminated after ALS-XZ");
        }
    }

    // ── ALS-XY-Wing ───────────────────────────────────────────────────────────

    @Test
    void findAlsXyWing_hardPuzzle_executesSearch() {
        load(TestPuzzles.HARD_PUZZLE);

        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.ALS_XY_WING);
            if (step != null) {
                assertEquals(SolutionType.ALS_XY_WING, step.getType());
            }
        });
    }

    // ── Death Blossom (best-effort) ───────────────────────────────────────────

    @Test
    void findDeathBlossom_hardPuzzle_executesSearch() {
        load(TestPuzzles.HARD_PUZZLE);

        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.DEATH_BLOSSOM);
            if (step != null) {
                assertEquals(SolutionType.DEATH_BLOSSOM, step.getType());
            }
        });
    }

    // ── Negative ──────────────────────────────────────────────────────────────

    @Test
    void als_easyPuzzle_searchRunsCleanly() {
        load(TestPuzzles.EASY_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.ALS_XZ),
                "ALS-XZ search on easy puzzle must not throw");
    }
}
