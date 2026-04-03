package solver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sudoku.SolutionType;
import sudoku.Sudoku2;
import testutil.TestPuzzles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link WingSolver} (XY-Wing, XYZ-Wing, W-Wing)
 * exercised via {@link SudokuStepFinder#getStep(SolutionType)}.
 */
class WingSolverTest {

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

    // ── XY-Wing ───────────────────────────────────────────────────────────────

    @Test
    void xyWing_puzzle_exercisesSearch() {
        load(TestPuzzles.XY_WING_PUZZLE);
        assertDoesNotThrow(() -> solver.solve());
    }

    // ── XYZ-Wing ──────────────────────────────────────────────────────────────

    @Test
    void xyzWing_puzzle_exercisesSearch() {
        load(TestPuzzles.XYZ_WING_PUZZLE);
        assertDoesNotThrow(() -> solver.solve());
    }

    // ── W-Wing ────────────────────────────────────────────────────────────────

    @Test
    void wWing_puzzle_exercisesSearch() {
        load(TestPuzzles.W_WING_PUZZLE);
        assertDoesNotThrow(() -> solver.solve());
    }

    // ── Negative ──────────────────────────────────────────────────────────────

    @Test
    void wings_easyPuzzle_searchRunsCleanly() {
        load(TestPuzzles.EASY_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.XY_WING),
                "XY-Wing search on easy puzzle must not throw");
    }
}

