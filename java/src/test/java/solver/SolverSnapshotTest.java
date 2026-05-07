package solver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sudoku.Candidate;
import sudoku.ClipboardMode;
import sudoku.SolutionStep;
import sudoku.SolutionType;
import sudoku.Sudoku2;
import testutil.TestPuzzles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Snapshot tests for migration to TypeScript.
 *
 * <p>Each test verifies the solver produces an exact, known output for a given input.
 * The TypeScript port must produce identical solution strings and elimination moves.
 *
 * <p><b>First-time setup:</b>
 * <ol>
 *   <li>Remove {@code @Disabled} from {@code GroundTruthPrinter.printGroundTruth()} and run:
 *       {@code ant test}
 *   <li>Copy the printed {@code private static final} lines here, replacing the
 *       {@code null} / {@code -1} sentinels.
 *   <li>Re-disable {@code GroundTruthPrinter}. Run: {@code ant test} — all tests here pass.
 * </ol>
 *
 * <p>When a sentinel remains, the test fails and prints the exact constant to paste in.
 */
class SolverSnapshotTest {

    // ── Solution snapshots (81-char strings; '.' = unsolved cell) ──────────
    private static final String EASY_SOLUTION         = "483921657967345821251876493548132976729564138136798245372689514814253769695417382";
    private static final String UNIQUENESS_1_SOLUTION = "579518243543279618812634975134752896925863754685491322268947531751385469397126587";
    private static final String NAKED_PAIR_SOLUTION   = "461572938732894156895316247378629514529481673614753892957248361183967425246135789";
    private static final String X_WING_SOLUTION       = "1..9........4.3.6...78..9.....69...2...7...3...638...............9.6....6....9.1.";
    private static final String XY_WING_SOLUTION      = "...6..5....8........9..3..6....6......6.5..9..4..8.62...47..........68..7....1...";
    private static final String SKYSCRAPER_SOLUTION   = ".97.5.....5...7.6...4..9.75......7..579.6.82...8.7....7..4..1..98.7...4.....9.687";
    private static final String REMOTE_PAIR_SOLUTION  = "...7...8..7..5.........8.3..9....4.3..2.3.9....3....6..8.3.........1..5..6...4...";
    private static final String ALS_XZ_SOLUTION       = "987523116356487912828916457615798334739145268144768519593674821471252683262351749";

    // ── Step elimination snapshots ─────────────────────────────────────────
    // ELIM_COUNT   = total candidates deleted by the step
    // ELIM_0_INDEX = cell index (0-80, row-major) of the first deletion
    // ELIM_0_VALUE = candidate digit (1-9) of the first deletion
    // Replace -1 with the values printed by GroundTruthPrinter.
    private static final int UR1_ELIM_COUNT          = 2;
    private static final int UR1_ELIM_0_INDEX        = 12;
    private static final int UR1_ELIM_0_VALUE        = 2;

    private static final int NAKED_PAIR_ELIM_COUNT   = 2;
    private static final int NAKED_PAIR_ELIM_0_INDEX = 31;
    private static final int NAKED_PAIR_ELIM_0_VALUE = 8;

    private static final int ALS_XZ_ELIM_COUNT       = 3;
    private static final int ALS_XZ_ELIM_0_INDEX     = 26;
    private static final int ALS_XZ_ELIM_0_VALUE     = 6;

    private static final int FINNED_X_WING_ELIM_COUNT   = 1;
    private static final int FINNED_X_WING_ELIM_0_INDEX = 11;
    private static final int FINNED_X_WING_ELIM_0_VALUE = 9;

    // ── Setup ──────────────────────────────────────────────────────────────

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
        Sudoku2 s = new Sudoku2();
        s.setSudoku(puzzle);
        solver.setSudoku(s);
    }

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

    // ── Solution string tests ──────────────────────────────────────────────

    @Test
    void solve_easyPuzzle_matchesSnapshot() {
        assertSolution(TestPuzzles.EASY_PUZZLE, EASY_SOLUTION, "EASY_SOLUTION");
    }

    @Test
    void solve_uniqueness1Puzzle_matchesSnapshot() {
        assertSolution(TestPuzzles.UNIQUENESS_1_PUZZLE, UNIQUENESS_1_SOLUTION, "UNIQUENESS_1_SOLUTION");
    }

    @Test
    void solve_nakedPairPuzzle_matchesSnapshot() {
        assertSolution(TestPuzzles.NAKED_PAIR_PUZZLE, NAKED_PAIR_SOLUTION, "NAKED_PAIR_SOLUTION");
    }

    @Test
    void solve_xWingPuzzle_matchesSnapshot() {
        assertSolution(TestPuzzles.X_WING_PUZZLE, X_WING_SOLUTION, "X_WING_SOLUTION");
    }

    @Test
    void solve_xyWingPuzzle_matchesSnapshot() {
        assertSolution(TestPuzzles.XY_WING_PUZZLE, XY_WING_SOLUTION, "XY_WING_SOLUTION");
    }

    @Test
    void solve_skyscraperPuzzle_matchesSnapshot() {
        assertSolution(TestPuzzles.SKYSCRAPER_PUZZLE, SKYSCRAPER_SOLUTION, "SKYSCRAPER_SOLUTION");
    }

    @Test
    void solve_remotePairPuzzle_matchesSnapshot() {
        assertSolution(TestPuzzles.REMOTE_PAIR_PUZZLE, REMOTE_PAIR_SOLUTION, "REMOTE_PAIR_SOLUTION");
    }

    @Test
    void solve_alsXzPuzzle_matchesSnapshot() {
        assertSolution(TestPuzzles.ALS_XZ_PUZZLE, ALS_XZ_SOLUTION, "ALS_XZ_SOLUTION");
    }

    // ── Step elimination tests ─────────────────────────────────────────────

    @Test
    void findUniqueness1_puzzle_matchesSnapshot() {
        load(TestPuzzles.UNIQUENESS_1_PUZZLE);
        assertStepElimination(SolutionType.UNIQUENESS_1,
                UR1_ELIM_COUNT, UR1_ELIM_0_INDEX, UR1_ELIM_0_VALUE, "UR1");
    }

    @Test
    void findNakedPair_puzzle_matchesSnapshot() {
        load(TestPuzzles.NAKED_PAIR_PUZZLE);
        assertStepElimination(SolutionType.NAKED_PAIR,
                NAKED_PAIR_ELIM_COUNT, NAKED_PAIR_ELIM_0_INDEX, NAKED_PAIR_ELIM_0_VALUE, "NAKED_PAIR");
    }

    @Test
    void findAlsXz_puzzle_matchesSnapshot() {
        load(TestPuzzles.ALS_XZ_PUZZLE);
        assertStepElimination(SolutionType.ALS_XZ,
                ALS_XZ_ELIM_COUNT, ALS_XZ_ELIM_0_INDEX, ALS_XZ_ELIM_0_VALUE, "ALS_XZ");
    }

    @Test
    void findFinnedXWing_puzzle_matchesSnapshot() {
        load(TestPuzzles.FINNED_X_WING_PUZZLE);
        advanceSimples(); // finned X-wing requires basics cleared first
        assertStepElimination(SolutionType.FINNED_X_WING,
                FINNED_X_WING_ELIM_COUNT, FINNED_X_WING_ELIM_0_INDEX, FINNED_X_WING_ELIM_0_VALUE, "FINNED_X_WING");
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void assertSolution(String puzzle, String expected, String constantName) {
        load(puzzle);
        solver.solve();
        Sudoku2 sudoku = finder.getSudoku();
        // Note: some puzzles are only partially solvable with logical techniques.
        // The snapshot captures the partial result — '.' denotes an unsolved cell.
        String actual = sudoku.getSudoku(ClipboardMode.VALUES_ONLY);
        assertEquals(81, actual.length(), "Solution string must be 81 characters");
        if (expected == null) {
            fail("Snapshot not yet captured. Run GroundTruthPrinter and set:\n"
                    + "    private static final String " + constantName + " = \"" + actual + "\";");
        }
        assertEquals(expected, actual, "Solver output must exactly match the reference implementation");
    }

    private void assertStepElimination(SolutionType type,
            int expectedCount, int expected0Index, int expected0Value, String prefix) {
        SolutionStep step = finder.getStep(type);
        assertNotNull(step, type + " step must be found on this puzzle");
        assertEquals(type, step.getType());
        List<Candidate> del = step.getCandidatesToDelete();
        assertFalse(del.isEmpty(), type + " step must eliminate at least one candidate");
        if (expectedCount == -1) {
            Candidate c0 = del.get(0);
            fail("Snapshot not yet captured. Run GroundTruthPrinter and set:\n"
                    + "    private static final int " + prefix + "_ELIM_COUNT   = " + del.size() + ";\n"
                    + "    private static final int " + prefix + "_ELIM_0_INDEX = " + c0.getIndex() + ";\n"
                    + "    private static final int " + prefix + "_ELIM_0_VALUE = " + c0.getValue() + ";");
        }
        assertEquals(expectedCount, del.size(),
                type + " must eliminate exactly " + expectedCount + " candidates");
        assertEquals(expected0Index, del.get(0).getIndex(),
                type + " first elimination must be at cell index " + expected0Index);
        assertEquals(expected0Value, del.get(0).getValue(),
                type + " first elimination digit must be " + expected0Value);
    }
}
