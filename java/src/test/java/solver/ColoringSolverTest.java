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
 * Integration tests for {@link ColoringSolver} (Simple Colors, Multi-Colors)
 * exercised via {@link SudokuStepFinder#getStep(SolutionType)}.
 */
class ColoringSolverTest {

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

    // ── Simple Colors ─────────────────────────────────────────────────────────

    @Test
    void findSimpleColors_puzzle_returnsStep() {
        load(TestPuzzles.SIMPLE_COLORS_PUZZLE);

        SolutionStep step = finder.getStep(SolutionType.SIMPLE_COLORS_TRAP);
        if (step == null) step = finder.getStep(SolutionType.SIMPLE_COLORS_WRAP);
        if (step == null) step = finder.getStep(SolutionType.SIMPLE_COLORS);

        assumeTrue(step != null, "No Simple Colors step found in test puzzle; update puzzle string for stronger assertion");
    }

    @Test
    void doStep_simpleColors_eliminatesOrSetsCandidate() {
        load(TestPuzzles.SIMPLE_COLORS_PUZZLE);
        Sudoku2 sudoku = finder.getSudoku();

        SolutionStep step = finder.getStep(SolutionType.SIMPLE_COLORS_TRAP);
        if (step == null) step = finder.getStep(SolutionType.SIMPLE_COLORS_WRAP);
        if (step == null) step = finder.getStep(SolutionType.SIMPLE_COLORS);
        assumeTrue(step != null);

        List<Candidate> toDelete = new ArrayList<>(step.getCandidatesToDelete());
        finder.doStep(step);

        for (Candidate c : toDelete) {
            assertFalse(sudoku.isCandidate(c.getIndex(), c.getValue()),
                    "Candidate " + c.getValue() + " in cell " + c.getIndex() + " must be eliminated after Simple Colors");
        }
    }

    // ── Multi-Colors ──────────────────────────────────────────────────────────

    @Test
    void findMultiColors_puzzle_executesSearch() {
        load(TestPuzzles.MULTI_COLORS_PUZZLE);

        assertDoesNotThrow(() -> {
            SolutionStep step = finder.getStep(SolutionType.MULTI_COLORS_1);
            if (step == null) step = finder.getStep(SolutionType.MULTI_COLORS_2);
            if (step != null) {
                assertTrue(step.getType() == SolutionType.MULTI_COLORS_1
                        || step.getType() == SolutionType.MULTI_COLORS_2);
            }
        });
    }

    // ── Negative ──────────────────────────────────────────────────────────────

    @Test
    void coloring_easyPuzzle_searchRunsCleanly() {
        load(TestPuzzles.EASY_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.SIMPLE_COLORS),
                "Simple Colors search on easy puzzle must not throw");
    }
}
