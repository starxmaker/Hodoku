/*
 * Copyright (C) 2019-20  PseudoFish
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

/**
 * TeaVM entry point for HoDoKu.
 *
 * <p>This class is a stripped-down alternative to {@link Main} that avoids JDK
 * APIs unsupported by TeaVM such as desktop logging/config bootstrapping and
 * XML-backed option loading.</p>
 */
public class TeaVMMain {

    /**
     * TeaVM entry point.
     *
     * @param args command-line arguments forwarded from the JavaScript host
     */
    public static void main(String[] args) {

        TeaVMStdout.install();

        Options.initDefaults();

        if (args == null || args.length == 0) {
            System.out.println("CLI-only mode: provide puzzle input or a batch command (use /h for help).");
            return;
        }

        Main.handleCliArgsTeaVM(args);
    }
}
