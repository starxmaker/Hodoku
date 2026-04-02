package testutil;

import sudoku.Options;

/**
 * Shared puzzle constants and setup utilities for solver/generator integration tests.
 */
public class TestPuzzles {

    /**
     * A well-known easy puzzle solvable with naked/hidden singles only.
     * 28 givens, unique solution.
     */
    public static final String EASY_PUZZLE =
            "003020600900305001001806400008102900700000008006708200002609500800203009005010300";

    /**
     * A hard puzzle requiring advanced solving techniques (Peter Norvig's "hard1").
     * Unique solution, not solvable by singles alone.
     */
    public static final String HARD_PUZZLE =
            "400000805030000000000700000020000060000080400000010000000603070500200000104000000";

    /**
     * A drastically under-constrained grid (only 1 given) that has many solutions.
     */
    public static final String MULTI_SOLUTION_PUZZLE =
            "100000000000000000000000000000000000000000000000000000000000000000000000000000000";

    /**
     * A contradictory puzzle (two 1s in the same row) with no solution.
     */
    public static final String NO_SOLUTION_PUZZLE =
            "110000000000000000000000000000000000000000000000000000000000000000000000000000000";

    /**
     * Ensures the {@link Options} singleton is initialised before each test.
     * Falls back to default in-memory options if no config file exists.
     */
    public static void setupOptions() {
        Options.getInstance();
    }
}
