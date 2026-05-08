export type HodokuDifficulty = 'Easy' | 'Medium' | 'Hard' | 'Unfair' | 'Extreme';

export type HodokuTechnique =
  | 'Full House'
  | 'Hidden Single'
  | 'Hidden Pair'
  | 'Hidden Triple'
  | 'Hidden Quadruple'
  | 'Naked Single'
  | 'Naked Pair'
  | 'Naked Triple'
  | 'Naked Quadruple'
  | 'Locked Pair'
  | 'Locked Triple'
  | 'Locked Candidates'
  | 'Locked Candidates Type 1 (Pointing)'
  | 'Locked Candidates Type 2 (Claiming)'
  | 'Skyscraper'
  | '2-String Kite'
  | 'Uniqueness Test 1'
  | 'Uniqueness Test 2'
  | 'Uniqueness Test 3'
  | 'Uniqueness Test 4'
  | 'Uniqueness Test 5'
  | 'Uniqueness Test 6'
  | 'Bivalue Universal Grave + 1'
  | 'XY-Wing'
  | 'XYZ-Wing'
  | 'W-Wing'
  | 'X-Chain'
  | 'XY-Chain'
  | 'Remote Pair'
  | 'Nice Loop/AIC'
  | 'Continuous Nice Loop'
  | 'Discontinuous Nice Loop'
  | 'X-Wing'
  | 'Swordfish'
  | 'Jellyfish'
  | 'Squirmbag'
  | 'Whale'
  | 'Leviathan'
  | 'Finned X-Wing'
  | 'Finned Swordfish'
  | 'Finned Jellyfish'
  | 'Finned Squirmbag'
  | 'Finned Whale'
  | 'Finned Leviathan'
  | 'Sashimi X-Wing'
  | 'Sashimi Swordfish'
  | 'Sashimi Jellyfish'
  | 'Sashimi Squirmbag'
  | 'Sashimi Whale'
  | 'Sashimi Leviathan'
  | 'Franken X-Wing'
  | 'Franken Swordfish'
  | 'Franken Jellyfish'
  | 'Franken Squirmbag'
  | 'Franken Whale'
  | 'Franken Leviathan'
  | 'Finned Franken X-Wing'
  | 'Finned Franken Swordfish'
  | 'Finned Franken Jellyfish'
  | 'Finned Franken Squirmbag'
  | 'Finned Franken Whale'
  | 'Finned Franken Leviathan'
  | 'Mutant X-Wing'
  | 'Mutant Swordfish'
  | 'Mutant Jellyfish'
  | 'Mutant Squirmbag'
  | 'Mutant Whale'
  | 'Mutant Leviathan'
  | 'Finned Mutant X-Wing'
  | 'Finned Mutant Swordfish'
  | 'Finned Mutant Jellyfish'
  | 'Finned Mutant Squirmbag'
  | 'Finned Mutant Whale'
  | 'Finned Mutant Leviathan'
  | 'Sue de Coq'
  | 'Almost Locked Set XZ-Rule'
  | 'Almost Locked Set XY-Wing'
  | 'Almost Locked Set XY-Chain'
  | 'Death Blossom'
  | 'Template Set'
  | 'Template Delete'
  | 'Forcing Chain'
  | 'Forcing Chain Contradiction'
  | 'Forcing Chain Verity'
  | 'Forcing Net'
  | 'Forcing Net Contradiction'
  | 'Forcing Net Verity'
  | 'Brute Force'
  | 'Incomplete Solution'
  | 'Give Up'
  | 'Grouped Nice Loop/AIC'
  | 'Grouped Continuous Nice Loop'
  | 'Grouped Discontinuous Nice Loop'
  | 'Empty Rectangle'
  | 'Hidden Rectangle'
  | 'Avoidable Rectangle Type 1'
  | 'Avoidable Rectangle Type 2'
  | 'AIC'
  | 'Grouped AIC'
  | 'Simple Colors'
  | 'Multi Colors'
  | 'Kraken Fish'
  | 'Turbot Fish'
  | 'Kraken Fish Type 1'
  | 'Kraken Fish Type 2'
  | 'Dual 2-String Kite'
  | 'Dual Empty Rectangle'
  | 'Simple Colors Trap'
  | 'Simple Colors Wrap'
  | 'Multi Colors 1'
  | 'Multi Colors 2';

export declare const HODOKU_TECHNIQUES: ReadonlySet<HodokuTechnique>;

export interface RatingControl {
  cancel(): void;
  readonly cancelled: boolean;
}

export interface BaseSudokuRating {
  difficulty: HodokuDifficulty;
  score: number;
  givenUp: boolean;
  bruteForced: boolean;
  unsolvable: boolean;
}

export interface BasePuzzle {
  puzzle: string;
  solution?: string;
}

export interface SudokuSolutionPathStep {
  stepNumber: number;
  technique: HodokuTechnique;
  notation: string;
}

export interface SudokuRating extends BaseSudokuRating, BasePuzzle {
  puzzleNumber: number;
  steps?: SudokuSolutionPathStep[];
}
  
export interface GeneratedSudoku extends BaseSudokuRating, BasePuzzle {
}

export interface GenerateSudokuOptions {
  minScore?: number;
  maxScore?: number;
  difficulty?: HodokuDifficulty;
  signal?: AbortSignal;
}

export interface GenerateSudokusOptions extends GenerateSudokuOptions {
  quantity?: number;
}

export declare function generateSudokus(
  options: GenerateSudokusOptions,
  onGenerated?: (generatedSudoku: GeneratedSudoku, control: RatingControl) => boolean | void,
): Promise<GeneratedSudoku[]>;

export declare function generateSudoku(
  options: GenerateSudokuOptions
): Promise<GeneratedSudoku | null>;

export interface BaseRateSudokuOptions {
  includeSolution?: boolean;
  maxScore?: number;
  signal?: AbortSignal;
}
export interface RateSudokusOptions extends BaseRateSudokuOptions {
  puzzles: string[];
}

export interface RateSudokuOptions extends BaseRateSudokuOptions {
  puzzle: string;
  includePath?: boolean;
}

export declare function rateSudoku(
  options: RateSudokuOptions
): Promise<SudokuRating | null>;

export declare function rateSudokus(
  options: RateSudokusOptions,
  onRating?: (rating: SudokuRating, control: RatingControl) => boolean | void,
): Promise<SudokuRating[]>;
