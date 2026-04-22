/*
 * Copyright (C) 2008-12  Bernhard Hobiger
 *
 * This file is part of HoDoKu.
 *
 * HoDoKu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoDoKu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoDoKu. If not, see <http://www.gnu.org/licenses/>.
 */

package sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;

/**
 * Headless replacement that approximates "find all" by solving once and
 * returning the discovered solution path.
 */
public class FindAllSteps implements Runnable {
    private List<SolutionStep> steps;
    private Sudoku2 sudoku;
    private List<SolutionType> testTypes;

    public FindAllSteps() {
        this(null, null, null);
    }

    public FindAllSteps(List<SolutionStep> steps, Sudoku2 sudoku, Object unusedDialog) {
        this.steps = steps;
        this.sudoku = sudoku;
    }

    public void setSteps(List<SolutionStep> steps) {
        this.steps = steps;
    }

    public void setSudoku(Sudoku2 sudoku) {
        this.sudoku = sudoku;
    }

    public void setTestType(List<SolutionType> testTypes) {
        this.testTypes = testTypes;
    }

    @Override
    public void run() {
        if (steps == null || sudoku == null) {
            return;
        }

        SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
        Sudoku2 working = sudoku.clone();
        solver.setSudoku(working);
        solver.solve();

        List<SolutionStep> solvedSteps = solver.getSteps();
        if (solvedSteps == null) {
            return;
        }

        List<SolutionStep> selected = new ArrayList<SolutionStep>();
        if (testTypes == null || testTypes.isEmpty()) {
            selected.addAll(solvedSteps);
        } else {
            for (SolutionStep step : solvedSteps) {
                if (testTypes.contains(step.getType())) {
                    selected.add(step);
                }
            }
        }

        steps.clear();
        steps.addAll(selected);
        Collections.sort(steps);
    }
}
