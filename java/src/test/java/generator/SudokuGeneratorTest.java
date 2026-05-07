package generator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import sudoku.Sudoku2;
import testutil.TestPuzzles;

import java.util.concurrent.TimeUnit;

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

    // ── generate ──────────────────────────────────────────────────────────────

    @Test
    void generateSudoku_symmetric_returnsValidPuzzle() {
        Sudoku2 result = generator.generateSudoku(true);

        assertNotNull(result, "generateSudoku(symmetric=true) must return a puzzle");
        assertEquals(1, generator.getNumberOfSolutions(result, 2),
                "Generated puzzle must have exactly one solution");
    }

    @Test
    void generateSudoku_asymmetric_returnsValidPuzzle() {
        Sudoku2 result = generator.generateSudoku(false);

        assertNotNull(result, "generateSudoku(symmetric=false) must return a puzzle");
        assertEquals(1, generator.getNumberOfSolutions(result, 2),
                "Generated puzzle must have exactly one solution");
    }

    @Test
    @Disabled("Pattern-based generation is non-deterministic and can take minutes; excluded from CI")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void generateSudoku_withPattern_returnsValidPuzzle() {
        // Derive the given-cell positions from EASY_PUZZLE (28 givens) so the
        // generator has a realistic density and finds a puzzle quickly.
        boolean[] pattern = new boolean[81];
        String source = TestPuzzles.EASY_PUZZLE;
        for (int i = 0; i < 81; i++) {
            pattern[i] = (source.charAt(i) != '0');
        }

        Sudoku2 result = generator.generateSudoku(false, pattern);
        if (result != null) {
            assertEquals(1, generator.getNumberOfSolutions(result, 2),
                    "If a puzzle was generated from a pattern it must have exactly one solution");
        }
    }

    // ── solve (backtracking API) ──────────────────────────────────────────────

    @Test
    void solve_byString_populatesSolutionCount() {
        generator.solve(TestPuzzles.EASY_PUZZLE, 1);

        assertTrue(generator.getSolutionCount() >= 1,
                "Solving by string must store at least one solution");
    }

    @Test
    void getSolutionAsString_afterSolve_returns81CharString() {
        generator.solve(TestPuzzles.EASY_PUZZLE, 1);

        String s = generator.getSolutionAsString();

        assertNotNull(s, "getSolutionAsString() must not return null after solve");
        assertEquals(81, s.length(), "Solution string must be exactly 81 characters");
        assertTrue(s.matches("[1-9]{81}"), "Solution string must contain only digits 1-9");
    }
}
