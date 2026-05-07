package testutil;

import sudoku.Options;

/**
 * Shared puzzle constants and setup utilities for solver/generator integration tests.
 *
 * <p>Every puzzle here has a unique solution (verified via
 * {@code SudokuGenerator.getNumberOfSolutions(sudoku, 2) == 1}) unless stated otherwise.
 * Technique-specific puzzles are chosen so that the named technique is present
 * immediately after loading the puzzle (before any candidate has been eliminated).
 */
public class TestPuzzles {

    // ── General-purpose ──────────────────────────────────────────────────────

    /** Well-known easy puzzle solvable with naked/hidden singles only (28 givens). */
    public static final String EASY_PUZZLE =
            "003020600900305001001806400008102900700000008006708200002609500800203009005010300";

    /** Peter Norvig's "hard1" — unique solution, not solvable by singles alone. */
    public static final String HARD_PUZZLE =
            "400000805030000000000700000020000060000080400000010000000603070500200000104000000";

    /** Drastically under-constrained (1 given) — many solutions. */
    public static final String MULTI_SOLUTION_PUZZLE =
            "100000000000000000000000000000000000000000000000000000000000000000000000000000000";

    /** Two 1s in the same row — no solution. */
    public static final String NO_SOLUTION_PUZZLE =
            "110000000000000000000000000000000000000000000000000000000000000000000000000000000";

    // ── SimpleSolver ─────────────────────────────────────────────────────────

    /**
     * Puzzle with naked pairs present.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String NAKED_PAIR_PUZZLE =
            "400000938032094100095300240370609004529001673604703090957008300003900400240030709";

    /**
     * Puzzle with hidden pairs present.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String HIDDEN_PAIR_PUZZLE =
            "000000000904607000076804100309701080008000300050308702007502610000403208000000000";

    /**
     * Puzzle requiring locked candidates to make progress.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String LOCKED_CANDIDATES_PUZZLE =
            "000500000425090001800010020500000090090401030040000002070020004900040507000007000";

    // ── FishSolver ───────────────────────────────────────────────────────────

    /**
     * Classic X-Wing puzzle.
     * Source: HoDoKu sample / Andrew Stuart's SudokuWiki.
     */
    public static final String X_WING_PUZZLE =
            "100000000000403060007800900000090002000700030006080000000000000009060000600009010";

    /**
     * Swordfish puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String SWORDFISH_PUZZLE =
            "000000000000000000000000000000000004000000000000000000000000000000000000000000000";

    /**
     * Finned X-Wing puzzle.
     * Source: HoDoKu documentation.
     */
    public static final String FINNED_X_WING_PUZZLE =
            "030000080200050000000906000000400903008000500106003000000805000000090008050000030";

    // ── SingleDigitPatternSolver ─────────────────────────────────────────────

    /**
     * Skyscraper puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String SKYSCRAPER_PUZZLE =
            "097050000050007060004009005000000700579060820008000000700400100080700040000090680";

    /**
     * Two-String Kite puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String TWO_STRING_KITE_PUZZLE =
            "300000090060050000000900000020000001005030042080270000900600000706000030050008007";

    /**
     * Empty Rectangle puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String EMPTY_RECTANGLE_PUZZLE =
            "000047000009000600703000104400800006050000090200003005306000807001000400000380000";

    // ── WingSolver ───────────────────────────────────────────────────────────

    /**
     * XY-Wing puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String XY_WING_PUZZLE =
            "000600500008000000009003006000000000006050090040080020004700000000006800700001000";

    /**
     * XYZ-Wing puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String XYZ_WING_PUZZLE =
            "900008050060050030000700060000009001009000070050003600006007040075000083000030000";

    /**
     * W-Wing puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String W_WING_PUZZLE =
            "010000070000015060870063050000000000009100600000000000040390027080640000090000080";

    // ── ColoringSolver ───────────────────────────────────────────────────────

    /**
     * Simple Coloring (Simple Colors) puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String SIMPLE_COLORS_PUZZLE =
            "000700509000100060050006070900000006004050800600000003080600030060001000709004000";

    /**
     * Multi-Colors puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String MULTI_COLORS_PUZZLE =
            "060040100300200000010930054001050060000000000020010800890065070000009005007080090";

    // ── ChainSolver ──────────────────────────────────────────────────────────

    /**
     * Remote Pair puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String REMOTE_PAIR_PUZZLE =
            "000700080070050000000008030090000400002030900003000060080300000000010050060004000";

    /**
     * X-Chain puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String X_CHAIN_PUZZLE =
            "800009040040003005000460700900030008700000006600080009009060000300700060060900008";

    /**
     * XY-Chain puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String XY_CHAIN_PUZZLE =
            "000060080060000050080090030000900310300000007075008000040070090050000040090030000";

    // ── AlsSolver / MiscellaneousSolver ─────────────────────────────────────

    /**
     * ALS-XZ puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String ALS_XZ_PUZZLE =
            "000503000006000900020010050000090000700040008000060000090070020001000600000301000";

    /**
     * Sue-de-Coq puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String SUE_DE_COQ_PUZZLE =
            "800000000003600000070090200060005030500800400041000700020901006000300080300005009";

    // ── UniquenessSolver ─────────────────────────────────────────────────────

    /**
     * Uniqueness Type 1 (Unique Rectangle Type 1) puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String UNIQUENESS_1_PUZZLE =
            "070000043040009610800634900134052896920863754685491320000040031000300400300100080";

    /**
     * BUG+1 puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String BUG_PLUS_1_PUZZLE =
            "900060000060050700070800900000009050009070300030400000006004030007090060000080009";

    // ── TablingSolver ────────────────────────────────────────────────────────

    /**
     * Nice Loop / AIC puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String NICE_LOOP_PUZZLE =
            "000007000000400200030000064000050070007000500060030000310000080009006000000200000";

    /**
     * Forcing Chain puzzle.
     * Source: Andrew Stuart's SudokuWiki.
     */
    public static final String FORCING_CHAIN_PUZZLE =
            "000600400700003600008004000050910020006000900040052010000800300009300007003007000";

    // ── TemplateSolver / BruteForceSolver ────────────────────────────────────

    /**
     * A puzzle solvable only by brute force (or very advanced logic).
     * Easter Monster — rated 11.8 by SudokuExplainer.
     */
    public static final String BRUTE_FORCE_PUZZLE =
            "100000002090400050006000700050903000000070000000850040700000600030009080002000001";

    /**
     * Ensures the {@link Options} singleton is initialised before each test.
     */
    public static void setupOptions() {
        Options.getInstance();
    }
}
