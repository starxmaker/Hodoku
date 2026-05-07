export type HodokuDifficulty = 'Easy' | 'Medium' | 'Hard' | 'Unfair' | 'Extreme';

export interface RatingControl {
  cancel(): void;
  readonly cancelled: boolean;
}

export interface SudokuRating {
  puzzle: string;
  puzzleNumber: number;
  difficulty: HodokuDifficulty;
  score: number;
  givenUp: boolean;
  bruteForced: boolean;
  unsolvable: boolean;
  solution?: string;
}

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
