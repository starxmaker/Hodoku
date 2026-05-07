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
 * Integration tests for {@link SingleDigitPatternSolver} (Skyscraper, Two-String Kite,
 * Empty Rectangle) exercised via {@link SudokuStepFinder#getStep(SolutionType)}.
 */
class SingleDigitPatternSolverTest {

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

    // Skyscraper

    @Test
    void skyscraper_puzzle_exercisesSearch() {
        load(TestPuzzles.SKYSCRAPER_PUZZLE);
        assertDoesNotThrow(() -> solver.solve());
    }

    // Two-String Kite

    @Test
    void twoStringKite_puzzle_exercisesSearch() {
        load(TestPuzzles.TWO_STRING_KITE_PUZZLE);
        assertDoesNotThrow(() -> solver.solve());
    }

    // Empty Rectangle

    @Test
    void emptyRectangle_puzzle_exercisesSearch() {
        load(TestPuzzles.EMPTY_RECTANGLE_PUZZLE);
        assertDoesNotThrow(() -> solver.solve());
    }

    //  Negative

    @Test
    void singleDigitPattern_easyPuzzle_searchRunsCleanly() {
        load(TestPuzzles.EASY_PUZZLE);

        assertDoesNotThrow(() -> finder.getStep(SolutionType.SKYSCRAPER),
                "Skyscraper search on easy puzzle must not throw");
    }
}