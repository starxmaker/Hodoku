package solver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sudoku.Sudoku2;
import testutil.TestPuzzles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Coverage-focused tests that call every {@link SudokuStepFinder} "find all steps"
 * API on representative puzzles. These tests do not assert specific step counts
 * but ensure that all search code paths are executed (and therefore instrumented
 * for JaCoCo), and that none of the searches throw unexpected exceptions.
 *
 * <p>Fish-type constants (from FishSolver):
 * <ul>
 *   <li>0 = BASIC (X-Wing … Jellyfish)</li>
 *   <li>1 = FRANKEN (mixed row/block bases)</li>
 *   <li>2 = MUTANT (any mix of units)</li>
 * </ul>
 */
class StepFinderCoverageTest {

    private SudokuSolver solver;
    private SudokuStepFinder finder;
    private Sudoku2 easy;
    private Sudoku2 hard;

    @BeforeEach
    void setUp() {
        TestPuzzles.setupOptions();
        solver = SudokuSolverFactory.getInstance();
        finder = solver.getStepFinder();

        easy = new Sudoku2();
        easy.setSudoku(TestPuzzles.EASY_PUZZLE);

        hard = new Sudoku2();
        hard.setSudoku(TestPuzzles.HARD_PUZZLE);
    }

    @AfterEach
    void tearDown() {
        SudokuSolverFactory.giveBack(solver);
    }

    // ── SimpleSolver (findAll*) ───────────────────────────────────────────────

    @Test
    void findAllFullHouses_easyPuzzle_returnsNotNull() {
        assertNotNull(finder.findAllFullHouses(easy));
    }

    @Test
    void findAllNakedSingles_easyPuzzle_returnsNonEmptyList() {
        List<?> steps = finder.findAllNakedSingles(easy);
        assertNotNull(steps);
        assertFalse(steps.isEmpty(), "Easy puzzle must have at least one Naked Single");
    }

    @Test
    void findAllHiddenSingles_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.findAllHiddenSingles(hard));
    }

    @Test
    void findAllNakedSubsets_easyPuzzle_returnsNotNull() {
        assertNotNull(finder.findAllNakedXle(easy));
    }

    @Test
    void findAllHiddenSubsets_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.findAllHiddenXle(hard));
    }

    @Test
    void findAllLockedCandidates_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.findAllLockedCandidates(hard));
        assertNotNull(finder.findAllLockedCandidates1(hard));
        assertNotNull(finder.findAllLockedCandidates2(hard));
    }

    // ── FishSolver (basic, finned, franken, mutant, kraken) ───────────────────

    @Test
    void getAllFishes_basic_easyPuzzle_returnsNotNull() {
        // type=0 → BASIC fish (X-Wing, Swordfish, Jellyfish)
        assertNotNull(finder.getAllFishes(easy, 2, 4, 0, 0, null, -1, 0));
    }

    @Test
    void getAllFishes_basic_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllFishes(hard, 2, 4, 0, 0, null, -1, 0));
    }

    @Test
    void getAllFishes_finned_hardPuzzle_returnsNotNull() {
        // maxFins/maxEndoFins = 2; type=0 → finned basic fish
        assertNotNull(finder.getAllFishes(hard, 2, 4, 2, 2, null, -1, 0));
    }

    @Test
    void getAllFishes_franken_hardPuzzle_returnsNotNull() {
        // type=1 → FRANKEN fish
        assertNotNull(finder.getAllFishes(hard, 2, 3, 2, 2, null, -1, 1));
    }

    @Test
    void getAllFishes_mutant_hardPuzzle_returnsNotNull() {
        // type=2 → MUTANT fish
        assertNotNull(finder.getAllFishes(hard, 2, 3, 2, 2, null, -1, 2));
    }

    @Test
    void getAllKrakenFishes_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllKrakenFishes(hard, 2, 3, 2, 2, null, -1, 0));
    }

    // ── SingleDigitPatternSolver (findAll*) ───────────────────────────────────

    @Test
    void findAllEmptyRectangles_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.findAllEmptyRectangles(hard));
    }

    @Test
    void findAllSkyscrapers_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.findAllSkyScrapers(hard));
    }

    @Test
    void findAllTwoStringKites_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.findAllTwoStringKites(hard));
    }

    // ── UniquenessSolver ──────────────────────────────────────────────────────

    @Test
    void getAllUniqueness_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllUniqueness(hard));
    }

    // ── WingSolver ────────────────────────────────────────────────────────────

    @Test
    void getAllWings_easyPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllWings(easy));
    }

    @Test
    void getAllWings_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllWings(hard));
    }

    // ── ColoringSolver ────────────────────────────────────────────────────────

    @Test
    void findAllSimpleColors_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.findAllSimpleColors(hard));
    }

    @Test
    void findAllMultiColors_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.findAllMultiColors(hard));
    }

    // ── ChainSolver ───────────────────────────────────────────────────────────

    @Test
    void getAllChains_easyPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllChains(easy));
    }

    @Test
    void getAllChains_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllChains(hard));
    }

    // ── AlsSolver ─────────────────────────────────────────────────────────────

    @Test
    void getAllAlses_xzOnly_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllAlses(hard, true, false, false));
    }

    @Test
    void getAllAlses_xyOnly_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllAlses(hard, false, true, false));
    }

    @Test
    void getAllAlses_chainOnly_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllAlses(hard, false, false, true));
    }

    @Test
    void getAllDeathBlossoms_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllDeathBlossoms(hard));
    }

    // ── MiscellaneousSolver ───────────────────────────────────────────────────

    @Test
    void getAllSueDeCoqs_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllSueDeCoqs(hard));
    }

    // ── TablingSolver ─────────────────────────────────────────────────────────

    @Test
    void getAllNiceLoops_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllNiceLoops(hard));
    }

    @Test
    void getAllGroupedNiceLoops_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllGroupedNiceLoops(hard));
    }

    @Test
    void getAllForcingChains_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllForcingChains(hard));
    }

    @Test
    void getAllForcingNets_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllForcingNets(hard));
    }

    // ── TemplateSolver ────────────────────────────────────────────────────────

    @Test
    void getAllTemplates_hardPuzzle_returnsNotNull() {
        assertNotNull(finder.getAllTemplates(hard));
    }

    // ── Cached accessor coverage ──────────────────────────────────────────────

    @Test
    void cachedAccessors_afterLoad_returnNonNull() {
        solver.setSudoku(hard);
        assertNotNull(finder.getCandidates());
        assertNotNull(finder.getPositions());
        assertNotNull(finder.getCandidatesAllowed());
        assertNotNull(finder.getEmptyCells());
    }
}
