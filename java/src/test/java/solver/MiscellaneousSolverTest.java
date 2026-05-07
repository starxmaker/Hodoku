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
 * Integration tests for {@link MiscellaneousSolver} (Sue-de-Coq)
 * exercised via {@link SudokuStepFinder#getStep(SolutionType)}.
 */
class MiscellaneousSolverTest {

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

    // ── Sue-de-Coq ────────────────────────────────────────────────────────────

    @Test
    void findSueDeCoq_puzzle_returnsStep() {
        load(TestPuzzles.SUE_DE_COQ_PUZZLE);

        SolutionStep step = finder.getStep(SolutionType.SUE_DE_COQ);

        assertNotNull(step, "Sue-de-Coq puzzle must expose a Sue-de-Coq step");
        assertEquals(SolutionType.SUE_DE_COQ, step.getType());
    }

    @Test
    void doStep_sueDeCoq_eliminatesCandidate() {
        load(TestPuzzles.SUE_DE_COQ_PUZZLE);
        Sudoku2 sudoku = finder.getSudoku();
        SolutionStep step = finder.getStep(SolutionType.SUE_DE_COQ);
        assertNotNull(step);

        List<Candidate> toDelete = new ArrayList<>(step.getCandidatesToDelete());
        finder.doStep(step);

        for (Candidate c : toDelete) {
            assertFalse(sudoku.isCandidate(c.getIndex(), c.getValue()),
                    "Candidate " + c.getValue() + " in cell " + c.getIndex() + " must be eliminated after Sue-de-Coq");
        }
    }

    // ── Negative ──────────────────────────────────────────────────────────────

    @Test
    void sueDeCoq_easyPuzzle_searchRunsCleanly() {
        load(TestPuzzles.EASY_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.SUE_DE_COQ),
                "Sue-de-Coq search on easy puzzle must not throw");
    }
}
