package solver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sudoku.DifficultyType;
import sudoku.Sudoku2;
import testutil.TestPuzzles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link SudokuSolver} via {@link SudokuSolverFactory}.
 */
class SudokuSolverTest {

    private SudokuSolver solver;

    @BeforeEach
    void setUp() {
        TestPuzzles.setupOptions();
        solver = SudokuSolverFactory.getInstance();
    }

    @AfterEach
    void tearDown() {
        SudokuSolverFactory.giveBack(solver);
    }

    // ── solveSinglesOnly ──────────────────────────────────────────────────────

    @Test
    void solveSinglesOnly_easyPuzzle_returnsTrue() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);

        assertTrue(solver.solveSinglesOnly(sudoku),
                "Easy puzzle should be fully solvable using singles only");
    }

    @Test
    void solveSinglesOnly_hardPuzzle_returnsFalse() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.HARD_PUZZLE);

        assertFalse(solver.solveSinglesOnly(sudoku),
                "Hard (Extreme-rated) puzzle should not be solvable using singles only");
    }

    // ── solve (full) ──────────────────────────────────────────────────────────

    @Test
    void solve_easyPuzzle_returnsTrue() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        solver.setSudoku(sudoku);

        assertTrue(solver.solve(), "Solver must report success for the easy puzzle");
    }

    @Test
    void solve_easyPuzzle_leavesNoEmptyCells() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        solver.setSudoku(sudoku);
        solver.solve();

        assertEquals(0, sudoku.getUnsolvedCellsAnz(),
                "All 81 cells must be filled after a successful solve");
    }

    @Test
    void solve_easyPuzzle_producesNonEmptyStepList() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        solver.setSudoku(sudoku);
        solver.solve();

        assertFalse(solver.getSteps().isEmpty(),
                "Step list must not be empty after solving");
    }

    @Test
    void solve_easyPuzzle_classifiesAsEasyOrMedium() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        solver.setSudoku(sudoku);
        solver.solve();

        DifficultyType type = solver.getLevel().getType();
        assertTrue(type == DifficultyType.EASY || type == DifficultyType.MEDIUM,
                "Easy puzzle should be rated Easy or Medium, but was: " + type);
    }

    @Test
    void solve_hardPuzzle_returnsTrue() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.HARD_PUZZLE);
        solver.setSudoku(sudoku);

        assertTrue(solver.solve(), "Solver must report success for the hard puzzle");
    }

    @Test
    void solve_hardPuzzle_leavesNoEmptyCells() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.HARD_PUZZLE);
        solver.setSudoku(sudoku);
        solver.solve();

        assertEquals(0, sudoku.getUnsolvedCellsAnz(),
                "All 81 cells must be filled after solving the hard puzzle");
    }
}
