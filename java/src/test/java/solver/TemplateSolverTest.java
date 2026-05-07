package solver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sudoku.SolutionStep;
import sudoku.SolutionType;
import sudoku.Sudoku2;
import testutil.TestPuzzles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link TemplateSolver}, {@link BruteForceSolver},
 * {@link IncompleteSolver}, and {@link GiveUpSolver},
 * exercised via {@link SudokuStepFinder#getStep(SolutionType)}.
 */
class TemplateSolverTest {

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

    // ── Template Set ─────────────────────────────────────────────────────────

    @Test
    void findTemplateSet_hardPuzzle_executesSearch() {
        load(TestPuzzles.HARD_PUZZLE);

        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.TEMPLATE_SET);
            if (step != null) {
                assertEquals(SolutionType.TEMPLATE_SET, step.getType());
            }
        });
    }

    // ── Template Del ─────────────────────────────────────────────────────────

    @Test
    void findTemplateDel_hardPuzzle_executesSearch() {
        load(TestPuzzles.HARD_PUZZLE);

        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.TEMPLATE_DEL);
            if (step != null) {
                assertEquals(SolutionType.TEMPLATE_DEL, step.getType());
            }
        });
    }

    // ── Brute Force ──────────────────────────────────────────────────────────

    @Test
    void findBruteForce_viaFullSolve_exercisesBruteForcePath() {
        // Use the Easter Monster (rated 11.8) — solver will exhaust all logical
        // techniques and fall back to BRUTE_FORCE.
        load(TestPuzzles.BRUTE_FORCE_PUZZLE);
        solver.solve();

        boolean hadBruteForce = solver.getSteps().stream()
                .anyMatch(s -> s.getType() == SolutionType.BRUTE_FORCE);
        assertTrue(hadBruteForce,
                "Easter Monster requires brute force; BRUTE_FORCE step must appear in solution");
    }

    @Test
    void bruteForce_afterFullSolve_puzzleIsComplete() {
        load(TestPuzzles.BRUTE_FORCE_PUZZLE);
        solver.solve();

        assertEquals(0, finder.getSudoku().getUnsolvedCellsAnz(),
                "After full solve (including brute force) all cells must be filled");
    }

    // ── GIVE_UP / INCOMPLETE (marker solvers) ────────────────────────────────

    @Test
    void giveUp_easyPuzzle_searchRunsCleanly() {
        load(TestPuzzles.EASY_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.GIVE_UP),
                "GiveUp search must not throw on any puzzle");
    }

    @Test
    void doStep_giveUp_doesNotThrow() {
        load(TestPuzzles.EASY_PUZZLE);
        SolutionStep step = finder.getStep(SolutionType.GIVE_UP);
        assertNotNull(step);

        assertDoesNotThrow(() -> finder.doStep(step),
                "doStep(GIVE_UP) must not throw");
    }

    @Test
    void incomplete_easyPuzzle_searchRunsCleanly() {
        load(TestPuzzles.EASY_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.INCOMPLETE),
                "Incomplete search must not throw on any puzzle");
    }

    @Test
    void doStep_incomplete_doesNotThrow() {
        load(TestPuzzles.EASY_PUZZLE);
        SolutionStep step = new SolutionStep(SolutionType.INCOMPLETE);

        assertDoesNotThrow(() -> finder.doStep(step),
                "doStep(INCOMPLETE) must not throw");
    }

    // ── Negative ─────────────────────────────────────────────────────────────

    @Test
    void templates_easyPuzzle_searchRunsCleanly() {
        load(TestPuzzles.EASY_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.TEMPLATE_SET),
                "Template search on easy puzzle must not throw");
    }
}
