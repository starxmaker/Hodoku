package generator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sudoku.Sudoku2;
import testutil.TestPuzzles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link SudokuGenerator} via {@link SudokuGeneratorFactory}.
 */
class SudokuGeneratorTest {

    private SudokuGenerator generator;

    @BeforeEach
    void setUp() {
        TestPuzzles.setupOptions();
        generator = SudokuGeneratorFactory.getInstance();
    }

    @AfterEach
    void tearDown() {
        SudokuGeneratorFactory.giveBack(generator);
    }

    // ── getNumberOfSolutions ──────────────────────────────────────────────────

    @Test
    void getNumberOfSolutions_uniquePuzzle_returnsOne() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);

        int count = generator.getNumberOfSolutions(sudoku, 2);

        assertEquals(1, count, "Easy puzzle must have exactly one solution");
    }

    @Test
    void getNumberOfSolutions_hardPuzzle_returnsOne() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.HARD_PUZZLE);

        int count = generator.getNumberOfSolutions(sudoku, 2);

        assertEquals(1, count, "Hard puzzle must have exactly one solution");
    }

    @Test
    void getNumberOfSolutions_multiSolutionPuzzle_returnsMoreThanOne() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.MULTI_SOLUTION_PUZZLE);

        int count = generator.getNumberOfSolutions(sudoku, 2);

        assertTrue(count >= 2, "Under-constrained puzzle should have more than one solution");
    }

    @Test
    void getNumberOfSolutions_noSolutionPuzzle_returnsZero() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.NO_SOLUTION_PUZZLE);

        int count = generator.getNumberOfSolutions(sudoku, 2);

        assertEquals(0, count, "Contradictory puzzle must have no solution");
    }

    // ── validSolution ─────────────────────────────────────────────────────────

    @Test
    void validSolution_uniquePuzzle_returnsTrue() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);

        assertTrue(generator.validSolution(sudoku),
                "Easy puzzle should be recognised as having a valid unique solution");
    }

    @Test
    void validSolution_multiSolutionPuzzle_returnsFalse() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.MULTI_SOLUTION_PUZZLE);

        assertFalse(generator.validSolution(sudoku),
                "Under-constrained puzzle should not be recognised as having a unique solution");
    }

    // ── solution stored in Sudoku2 after getNumberOfSolutions ────────────────

    @Test
    void getNumberOfSolutions_uniquePuzzle_storesSolutionInSudoku() {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(TestPuzzles.EASY_PUZZLE);

        generator.getNumberOfSolutions(sudoku, 2);

        int[] solution = sudoku.getSolution();
        assertNotNull(solution, "Solution array must be stored in Sudoku2 after solving a unique puzzle");
        assertEquals(Sudoku2.LENGTH, solution.length,
                "Solution array must have one entry per cell (81)");
        for (int i = 0; i < solution.length; i++) {
            assertTrue(solution[i] >= 1 && solution[i] <= 9,
                    "Every cell must hold a digit 1-9, but cell " + i + " = " + solution[i]);
        }
    }
}
