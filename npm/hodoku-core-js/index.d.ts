export interface HodokuRating {
  puzzle: string;
  level: HodokuDifficulty;
  score: number;
  solved: boolean;
  requiresGuessing: boolean;
  requiresTemplates: boolean;
  gaveUp: boolean;
}

export type HodokuDifficulty = 'Incomplete' | 'Easy' | 'Medium' | 'Hard' | 'Unfair' | 'Extreme';

export interface HodokuMaxScoreRating extends HodokuRating {
  maxScore: number;
  withinMaxScore: boolean;
}

export interface HodokuDifficultyRating extends HodokuRating {
  maxDifficulty: HodokuDifficulty;
  withinDifficultyCap: boolean;
}

export declare function executeCommand(commandParts: string[]): Promise<string[]>;
export declare function rateSudoku(puzzle: string): Promise<HodokuRating>;
export declare function rateSudokus(puzzles: string[]): Promise<HodokuRating[]>;
export declare function rateSudokuWithMaxScore(puzzle: string, maxScore: number): Promise<HodokuMaxScoreRating>;
export declare function rateSudokusWithMaxScore(
  puzzles: string[],
  maxScore: number,
): Promise<HodokuMaxScoreRating[]>;
export declare function rateSudokuWithDifficulty(
  puzzle: string,
  maxDifficulty: HodokuDifficulty,
): Promise<HodokuDifficultyRating>;
export declare function rateSudokusWithDifficulty(
  puzzles: string[],
  maxDifficulty: HodokuDifficulty,
): Promise<HodokuDifficultyRating[]>;

export default rateSudoku;
