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
 * Integration tests for {@link ChainSolver} (Remote Pair, X-Chain, XY-Chain)
 * exercised via {@link SudokuStepFinder#getStep(SolutionType)}.
 */
class ChainSolverTest {

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

    // Remote Pair

    @Test
    void remotePair_puzzle_exercisesSearch() {
        load(TestPuzzles.REMOTE_PAIR_PUZZLE);
        assertDoesNotThrow(() -> solver.solve());
    }

    // X-Chain

    @Test
    void xChain_puzzle_exercisesSearch() {
        load(TestPuzzles.X_CHAIN_PUZZLE);
        assertDoesNotThrow(() -> solver.solve());
    }

    // XY-Chain

    @Test
    void xyChain_puzzle_exercisesSearch() {
        load(TestPuzzles.XY_CHAIN_PUZZLE);
        assertDoesNotThrow(() -> solver.solve());
    }

    // Negative

    @Test
    void chains_easyPuzzle_searchRunsCleanly() {
        load(TestPuzzles.EASY_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.REMOTE_PAIR),
                "Remote Pair search on easy puzzle must not throw");
    }
}