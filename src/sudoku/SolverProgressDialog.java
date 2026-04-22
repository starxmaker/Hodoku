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

import solver.SudokuSolver;

/**
 * Headless progress dialog replacement that runs solving in a worker thread.
 */
public class SolverProgressDialog {
    private final Thread thread;
    private volatile boolean solved;
    private volatile boolean visible;

    public SolverProgressDialog(Object parent, boolean modal, final SudokuSolver solver) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                solved = solver.solve();
            }
        }, "solver-progress");
        thread.start();
    }

    public Thread getThread() {
        return thread;
    }

    public void initializeProgressState(int anzCand) {
        // no-op in CLI mode
    }

    public void setProgressState(int unsolvedCells, int unsolvedCandidates) {
        // no-op in CLI mode
    }

    public boolean isSolved() {
        return solved;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
