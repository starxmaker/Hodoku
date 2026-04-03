package testutil;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;
import solver.SudokuStepFinder;
import sudoku.Candidate;
import sudoku.ClipboardMode;
import sudoku.SolutionStep;
import sudoku.SolutionType;
import sudoku.Sudoku2;

import java.util.List;

/**
 * Utility for capturing ground-truth outputs from the Java reference implementation.
 *
 * <p>When migrating the solver to TypeScript, run this printer once to obtain the
 * exact constants needed by {@link solver.SolverSnapshotTest}.
 *
 * <p><b>Usage:</b>
 * <ol>
 *   <li>Remove {@code @Disabled} from {@link #printGroundTruth()}.
 *   <li>Run: {@code ant test}
 *   <li>Copy the printed {@code private static final} declarations into
 *       {@code SolverSnapshotTest.java}, replacing the {@code null} / {@code -1} sentinels.
 *   <li>Re-add {@code @Disabled}.
 * </ol>
 */
class GroundTruthPrinter {

    @Test
    @Disabled("Enable once to capture ground truth; re-disable after copying output to SolverSnapshotTest")
    void printGroundTruth() {
        TestPuzzles.setupOptions();
        System.out.println();
        System.out.println("=== GROUND TRUTH - paste into SolverSnapshotTest.java ===");
        System.out.println();
        System.out.println("// Solution snapshots (replace null values):");
        printSolution("EASY",         TestPuzzles.EASY_PUZZLE);
        printSolution("UNIQUENESS_1", TestPuzzles.UNIQUENESS_1_PUZZLE);
        printSolution("NAKED_PAIR",   TestPuzzles.NAKED_PAIR_PUZZLE);
        printSolution("X_WING",       TestPuzzles.X_WING_PUZZLE);
        printSolution("XY_WING",      TestPuzzles.XY_WING_PUZZLE);
        printSolution("SKYSCRAPER",   TestPuzzles.SKYSCRAPER_PUZZLE);
        printSolution("REMOTE_PAIR",  TestPuzzles.REMOTE_PAIR_PUZZLE);
        printSolution("ALS_XZ",       TestPuzzles.ALS_XZ_PUZZLE);
        System.out.println();
        System.out.println("// Step elimination snapshots (replace -1 values):");
        printStep("UR1",           TestPuzzles.UNIQUENESS_1_PUZZLE,  SolutionType.UNIQUENESS_1,  false);
        printStep("NAKED_PAIR",    TestPuzzles.NAKED_PAIR_PUZZLE,    SolutionType.NAKED_PAIR,    false);
        printStep("ALS_XZ",        TestPuzzles.ALS_XZ_PUZZLE,        SolutionType.ALS_XZ,        false);
        printStep("FINNED_X_WING", TestPuzzles.FINNED_X_WING_PUZZLE, SolutionType.FINNED_X_WING, true);
        System.out.println();
        System.out.println("=== END GROUND TRUTH ===");
    }

    private void printSolution(String name, String puzzle) {
        SudokuSolver solver = SudokuSolverFactory.getInstance();
        try {
            Sudoku2 sudoku = new Sudoku2();
            sudoku.setSudoku(puzzle);
            solver.setSudoku(sudoku);
            solver.solve();
            String sol = solver.getStepFinder().getSudoku().getSudoku(ClipboardMode.VALUES_ONLY);
            System.out.printf("private static final String %s_SOLUTION = \"%s\";%n", name, sol);
        } finally {
            SudokuSolverFactory.giveBack(solver);
        }
    }

    private void printStep(String name, String puzzle, SolutionType type, boolean advanceFirst) {
        SudokuSolver solver = SudokuSolverFactory.getInstance();
        try {
            Sudoku2 sudoku = new Sudoku2();
            sudoku.setSudoku(puzzle);
            solver.setSudoku(sudoku);
            SudokuStepFinder finder = solver.getStepFinder();
            if (advanceFirst) {
                advanceSimples(finder);
            }
            SolutionStep step = finder.getStep(type);
            if (step == null) {
                System.out.printf("// %s: step was null (advanceFirst=%b) -- check puzzle%n", name, advanceFirst);
                return;
            }
            List<Candidate> del = step.getCandidatesToDelete();
            System.out.printf("private static final int %s_ELIM_COUNT   = %d;%n", name, del.size());
            if (!del.isEmpty()) {
                System.out.printf("private static final int %s_ELIM_0_INDEX = %d;%n", name, del.get(0).getIndex());
                System.out.printf("private static final int %s_ELIM_0_VALUE = %d;%n", name, del.get(0).getValue());
            }
        } finally {
            SudokuSolverFactory.giveBack(solver);
        }
    }

    private void advanceSimples(SudokuStepFinder finder) {
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
}
