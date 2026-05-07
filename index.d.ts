export type HodokuDifficulty = 'Easy' | 'Medium' | 'Hard' | 'Unfair' | 'Extreme';

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

export interface SudokuRating extends BaseSudokuRating, BasePuzzle {
  puzzleNumber: number;
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
}

export declare function rateSudoku(
  options: RateSudokuOptions
): Promise<SudokuRating | null>;

export declare function rateSudokus(
  options: RateSudokusOptions,
  onRating?: (rating: SudokuRating, control: RatingControl) => boolean | void,
): Promise<SudokuRating[]>;
