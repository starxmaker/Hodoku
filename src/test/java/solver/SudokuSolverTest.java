package solver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sudoku.DifficultyLevel;
import sudoku.DifficultyType;
import sudoku.Options;
import sudoku.SolutionStep;
import sudoku.SolutionType;
import sudoku.StepConfig;
import sudoku.Sudoku2;
import testutil.TestPuzzles;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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

    // ── getHint ───────────────────────────────────────────────────────────────

    @Test
    void getHint_easyPuzzle_returnsNonNullStep() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);

        SolutionStep hint = solver.getHint(sudoku, false);

        assertNotNull(hint, "getHint() must return a step for an unsolved puzzle");
    }

    @Test
    void doStep_hint_reducesUnsolvedCount() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        int before = sudoku.getUnsolvedCellsAnz();

        SolutionStep hint = solver.getHint(sudoku, false);
        assertNotNull(hint);
        solver.doStep(sudoku, hint);

        assertTrue(sudoku.getUnsolvedCellsAnz() < before || !hint.getCandidatesToDelete().isEmpty(),
                "Applying a hint must either set a cell or eliminate a candidate");
    }

    // ── solve(DifficultyLevel) ────────────────────────────────────────────────

    @Test
    void solve_withMaxDifficultyLevel_returnsTrue() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        DifficultyLevel maxLevel = Options.getInstance().getDifficultyLevels()[
                Options.getInstance().getDifficultyLevels().length - 1];

        boolean solved = solver.solve(maxLevel, sudoku, false, null);

        assertTrue(solved, "solve(DifficultyLevel) must succeed for the easy puzzle at max level");
    }

    // ── getLevel / getScore ───────────────────────────────────────────────────

    @Test
    void getLevel_afterSolve_isNotNull() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        solver.setSudoku(sudoku);
        solver.solve();

        assertNotNull(solver.getLevel(), "Difficulty level must be assigned after solving");
    }

    @Test
    void getScore_afterSolve_isPositive() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        solver.setSudoku(sudoku);
        solver.solve();

        assertTrue(solver.getScore() > 0, "Score must be positive after solving");
    }

    // ── setSudoku with partial steps ──────────────────────────────────────────

    @Test
    void setSudoku_withPartialSteps_preservesList() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        List<SolutionStep> partial = new ArrayList<>();
        partial.add(solver.getHint(sudoku, false));

        solver.setSudoku(sudoku, partial);

        assertEquals(1, solver.getSteps().size(),
                "setSudoku(Sudoku2, List) must preserve the provided step list");
    }

    // ── solve(boolean) ────────────────────────────────────────────────────────

    @Test
    void solve_withFalseBooleanVariant_returnsTrue() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        solver.setSudoku(sudoku);

        assertTrue(solver.solve(false), "solve(false) must succeed for the easy puzzle");
    }

    // ── solveWithSteps ────────────────────────────────────────────────────────

    @Test
    void solveWithSteps_easyPuzzle_returnsTrue() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);

        assertTrue(solver.solveWithSteps(sudoku, Options.getInstance().solverSteps),
                "solveWithSteps must report success for the easy puzzle");
    }

    // ── getAnzUsedSteps / getAnzSteps / getLevelString / getMaxLevel ──────────

    @Test
    void getAnzUsedSteps_afterSolve_isPositive() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        solver.setSudoku(sudoku);
        solver.solve();

        assertTrue(solver.getAnzUsedSteps() > 0,
                "At least one technique type must have been used");
    }

    @Test
    void getAnzSteps_afterSolve_isNonNull() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        solver.setSudoku(sudoku);
        solver.solve();

        assertNotNull(solver.getAnzSteps());
    }

    @Test
    void getLevelString_afterSolve_isNonNull() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        solver.setSudoku(sudoku);
        solver.solve();

        assertNotNull(solver.getLevelString());
    }

    @Test
    void getMaxLevel_isNonNull() {
        assertNotNull(solver.getMaxLevel());
    }

    @Test
    void getStepsNanoTime_isNonNull() {
        assertNotNull(solver.getStepsNanoTime());
    }

    // ── getCategory / getCategoryName ─────────────────────────────────────────

    @Test
    void getCategory_nakedSingle_returnsNonNull() {
        assertNotNull(solver.getCategory(SolutionType.NAKED_SINGLE));
    }

    @Test
    void getCategoryName_nakedSingle_returnsNonNull() {
        assertNotNull(solver.getCategoryName(SolutionType.NAKED_SINGLE));
    }

    // ── setters/getters roundtrip ─────────────────────────────────────────────

    @Test
    void setLevel_andGetLevel_roundtrip() {
        DifficultyLevel level = Options.getInstance().getDifficultyLevel(DifficultyType.HARD.ordinal());
        solver.setLevel(level);
        assertEquals(level, solver.getLevel());
    }

    @Test
    void setScore_andGetScore_roundtrip() {
        solver.setScore(1234);
        assertEquals(1234, solver.getScore());
    }

    @Test
    void setMaxLevel_andGetMaxLevel_roundtrip() {
        DifficultyLevel level = Options.getInstance().getDifficultyLevel(DifficultyType.EXTREME.ordinal());
        solver.setMaxLevel(level);
        assertEquals(level, solver.getMaxLevel());
    }

    @Test
    void setSteps_andGetSteps_roundtrip() {
        List<SolutionStep> list = new ArrayList<>();
        solver.setSteps(list);
        assertSame(list, solver.getSteps());
    }

    @Test
    void setAnzSteps_andGetAnzSteps_roundtrip() {
        int[] arr = new int[Options.getInstance().solverSteps.length];
        solver.setAnzSteps(arr);
        assertSame(arr, solver.getAnzSteps());
    }

    // ── printStatistics ───────────────────────────────────────────────────────

    @Test
    void printStatistics_printStream_doesNotThrow() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        solver.setSudoku(sudoku);
        solver.solve();

        assertDoesNotThrow(() -> solver.printStatistics(new PrintStream(new ByteArrayOutputStream())));
    }

    @Test
    void printStatistics_printWriter_doesNotThrow() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);
        solver.setSudoku(sudoku);
        solver.solve();

        assertDoesNotThrow(() -> solver.printStatistics(new PrintWriter(new StringWriter())));
    }
}
