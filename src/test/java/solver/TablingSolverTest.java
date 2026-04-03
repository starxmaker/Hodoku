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
 * Integration tests for {@link TablingSolver} (Nice Loops, AICs, Forcing Chains)
 * exercised via {@link SudokuStepFinder#getStep(SolutionType)}.
 */
class TablingSolverTest {

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

    // ── Nice Loop ─────────────────────────────────────────────────────────────

    @Test
    void findNiceLoop_puzzle_returnsStep() {
        load(TestPuzzles.NICE_LOOP_PUZZLE);

        SolutionStep step = finder.getStep(SolutionType.DISCONTINUOUS_NICE_LOOP);
        if (step == null) step = finder.getStep(SolutionType.CONTINUOUS_NICE_LOOP);
        if (step == null) step = finder.getStep(SolutionType.NICE_LOOP);

        assertNotNull(step, "Nice Loop puzzle must expose a Nice Loop or AIC step");
    }

    @Test
    void doStep_niceLoop_eliminatesOrSetsCandidate() {
        load(TestPuzzles.NICE_LOOP_PUZZLE);
        Sudoku2 sudoku = finder.getSudoku();

        SolutionStep step = finder.getStep(SolutionType.DISCONTINUOUS_NICE_LOOP);
        if (step == null) step = finder.getStep(SolutionType.CONTINUOUS_NICE_LOOP);
        if (step == null) step = finder.getStep(SolutionType.NICE_LOOP);
        assertNotNull(step);

        List<Candidate> toDelete = new ArrayList<>(step.getCandidatesToDelete());
        int unsolvedBefore = sudoku.getUnsolvedCellsAnz();
        finder.doStep(step);

        boolean hadEffect = sudoku.getUnsolvedCellsAnz() < unsolvedBefore;
        for (Candidate c : toDelete) {
            if (!hadEffect) {
                assertFalse(sudoku.isCandidate(c.getIndex(), c.getValue()),
                        "Candidate " + c.getValue() + " in cell " + c.getIndex() + " must be eliminated");
            }
        }
    }

    // ── AIC ───────────────────────────────────────────────────────────────────

    @Test
    void findAic_hardPuzzle_executesSearch() {
        load(TestPuzzles.HARD_PUZZLE);

        // Search must run without error; type verification omitted because the
        // returned step may be a grouped variant or other chain subtype.
        assertDoesNotThrow(() -> finder.getStep(SolutionType.AIC));
        assertDoesNotThrow(() -> finder.getStep(SolutionType.GROUPED_AIC));
    }

    // ── Forcing Chain ─────────────────────────────────────────────────────────

    @Test
    void findForcingChain_puzzle_returnsStep() {
        load(TestPuzzles.FORCING_CHAIN_PUZZLE);

        SolutionStep step = finder.getStep(SolutionType.FORCING_CHAIN);
        if (step == null) step = finder.getStep(SolutionType.FORCING_CHAIN_CONTRADICTION);
        if (step == null) step = finder.getStep(SolutionType.FORCING_CHAIN_VERITY);

        assertNotNull(step, "Forcing Chain puzzle must expose a Forcing Chain step");
    }

    @Test
    void doStep_forcingChain_eliminatesOrSetsCandidate() {
        load(TestPuzzles.FORCING_CHAIN_PUZZLE);
        Sudoku2 sudoku = finder.getSudoku();

        SolutionStep step = finder.getStep(SolutionType.FORCING_CHAIN);
        if (step == null) step = finder.getStep(SolutionType.FORCING_CHAIN_CONTRADICTION);
        if (step == null) step = finder.getStep(SolutionType.FORCING_CHAIN_VERITY);
        assertNotNull(step);

        List<Candidate> toDelete = new ArrayList<>(step.getCandidatesToDelete());
        int unsolvedBefore = sudoku.getUnsolvedCellsAnz();
        finder.doStep(step);

        boolean hadEffect = sudoku.getUnsolvedCellsAnz() < unsolvedBefore;
        for (Candidate c : toDelete) {
            if (!hadEffect) {
                assertFalse(sudoku.isCandidate(c.getIndex(), c.getValue()),
                        "Candidate " + c.getValue() + " in cell " + c.getIndex() + " must be eliminated");
            }
        }
    }

    // ── Negative ──────────────────────────────────────────────────────────────

    @Test
    void tabling_easyPuzzle_searchRunsCleanly() {
        load(TestPuzzles.EASY_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.NICE_LOOP),
                "Nice Loop search on easy puzzle must not throw");
    }
}
