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

/**
 *
 * @author hobiwan
 */
public enum SolutionType {

	FULL_HOUSE("Full House", "0000", "fh"),
	HIDDEN_SINGLE("Hidden Single", "0002", "h1"),
	HIDDEN_PAIR("Hidden Pair", "0210", "h2"),
	HIDDEN_TRIPLE("Hidden Triple", "0211", "h3"),
	HIDDEN_QUADRUPLE("Hidden Quadruple", "0212", "h4"),
	NAKED_SINGLE("Naked Single", "0003", "n1"),
	NAKED_PAIR("Naked Pair", "0200", "n2"),
	NAKED_TRIPLE("Naked Triple", "0201", "n3"),
	NAKED_QUADRUPLE("Naked Quadruple", "0202", "n4"),
	LOCKED_PAIR("Locked Pair", "0110", "l2"),
	LOCKED_TRIPLE("Locked Triple", "0111", "l3"),
	LOCKED_CANDIDATES("Locked Candidates", "xxxx", "lc"),
	LOCKED_CANDIDATES_1("Locked Candidates Type 1 (Pointing)", "0100", "lc1"),
	LOCKED_CANDIDATES_2("Locked Candidates Type 2 (Claiming)", "0101", "lc2"),
	SKYSCRAPER("Skyscraper", "0400", "sk"),
	TWO_STRING_KITE("2-String Kite", "0401", "2sk"),
	UNIQUENESS_1("Uniqueness Test 1", "0600", "u1"),
	UNIQUENESS_2("Uniqueness Test 2", "0601", "u2"),
	UNIQUENESS_3("Uniqueness Test 3", "0602", "u3"),
	UNIQUENESS_4("Uniqueness Test 4", "0603", "u4"),
	UNIQUENESS_5("Uniqueness Test 5", "0604", "u5"),
	UNIQUENESS_6("Uniqueness Test 6", "0605", "u6"),
	BUG_PLUS_1("Bivalue Universal Grave + 1", "0610", "bug1"),
	XY_WING("XY-Wing", "0800", "xy"),
	XYZ_WING("XYZ-Wing", "0801", "xyz"),
	W_WING("W-Wing", "0803", "w"),
	X_CHAIN("X-Chain", "0701", "x"),
	XY_CHAIN("XY-Chain", "0702", "xyc"),
	REMOTE_PAIR("Remote Pair", "0703", "rp"),
	NICE_LOOP("Nice Loop/AIC", "xxxx", "nl"),
	CONTINUOUS_NICE_LOOP("Continuous Nice Loop", "0706", "cnl"),
	DISCONTINUOUS_NICE_LOOP("Discontinuous Nice Loop", "0707", "dnl"),
	X_WING("X-Wing", "0300", "bf2"),
	SWORDFISH("Swordfish", "0301", "bf3"),
	JELLYFISH("Jellyfish", "0302", "bf4"),
	SQUIRMBAG("Squirmbag", "0303", "bf5"),
	WHALE("Whale", "0304", "bf6"),
	LEVIATHAN("Leviathan", "0305", "bf7"),
	FINNED_X_WING("Finned X-Wing", "0310", "fbf2"),
	FINNED_SWORDFISH("Finned Swordfish", "0311", "fbf3"),
	FINNED_JELLYFISH("Finned Jellyfish", "0312", "fbf4"),
	FINNED_SQUIRMBAG("Finned Squirmbag", "0313", "fbf5"),
	FINNED_WHALE("Finned Whale", "0314", "fbf6"),
	FINNED_LEVIATHAN("Finned Leviathan", "0315", "fbf7"),
	SASHIMI_X_WING("Sashimi X-Wing", "0320", "sbf2"),
	SASHIMI_SWORDFISH("Sashimi Swordfish", "0321", "sbf3"),
	SASHIMI_JELLYFISH("Sashimi Jellyfish", "0322", "sbf4"),
	SASHIMI_SQUIRMBAG("Sashimi Squirmbag", "0323", "sbf5"),
	SASHIMI_WHALE("Sashimi Whale", "0324", "sbf6"),
	SASHIMI_LEVIATHAN("Sashimi Leviathan", "0325", "sbf7"),
	FRANKEN_X_WING("Franken X-Wing", "0330", "ff2"),
	FRANKEN_SWORDFISH("Franken Swordfish", "0331", "ff3"),
	FRANKEN_JELLYFISH("Franken Jellyfish", "0332", "ff4"),
	FRANKEN_SQUIRMBAG("Franken Squirmbag", "0333", "ff5"),
	FRANKEN_WHALE("Franken Whale", "0334", "ff6"),
	FRANKEN_LEVIATHAN("Franken Leviathan", "0335", "ff7"),
	FINNED_FRANKEN_X_WING("Finned Franken X-Wing", "0340", "fff2"),
	FINNED_FRANKEN_SWORDFISH("Finned Franken Swordfish", "0341", "fff3"),
	FINNED_FRANKEN_JELLYFISH("Finned Franken Jellyfish", "0342", "fff4"),
	FINNED_FRANKEN_SQUIRMBAG("Finned Franken Squirmbag", "0343", "fff5"),
	FINNED_FRANKEN_WHALE("Finned Franken Whale", "0344", "fff6"),
	FINNED_FRANKEN_LEVIATHAN("Finned Franken Leviathan", "0345", "fff7"),
	MUTANT_X_WING("Mutant X-Wing", "0350", "mf2"),
	MUTANT_SWORDFISH("Mutant Swordfish", "0351", "mf3"),
	MUTANT_JELLYFISH("Mutant Jellyfish", "0352", "mf4"),
	MUTANT_SQUIRMBAG("Mutant Squirmbag", "0353", "mf5"),
	MUTANT_WHALE("Mutant Whale", "0354", "mf6"),
	MUTANT_LEVIATHAN("Mutant Leviathan", "0355", "mf7"),
	FINNED_MUTANT_X_WING("Finned Mutant X-Wing", "0360", "fmf2"),
	FINNED_MUTANT_SWORDFISH("Finned Mutant Swordfish", "0361", "fmf3"),
	FINNED_MUTANT_JELLYFISH("Finned Mutant Jellyfish", "0362", "fmf4"),
	FINNED_MUTANT_SQUIRMBAG("Finned Mutant Squirmbag", "0363", "fmf5"),
	FINNED_MUTANT_WHALE("Finned Mutant Whale", "0364", "fmf6"),
	FINNED_MUTANT_LEVIATHAN("Finned Mutant Leviathan", "0365", "fmf7"),
	SUE_DE_COQ("Sue de Coq", "1101", "sdc"),
	ALS_XZ("Almost Locked Set XZ-Rule", "0901", "axz"),
	ALS_XY_WING("Almost Locked Set XY-Wing", "0902", "axy"),
	ALS_XY_CHAIN("Almost Locked Set XY-Chain", "0903", "ach"),
	DEATH_BLOSSOM("Death Blossom", "0904", "db"),
	TEMPLATE_SET("Template Set", "1201", "ts"),
	TEMPLATE_DEL("Template Delete", "1202", "td"),
	FORCING_CHAIN("Forcing Chain", "xxxx", "fc"),
	FORCING_CHAIN_CONTRADICTION("Forcing Chain Contradiction", "1301", "fcc"),
	FORCING_CHAIN_VERITY("Forcing Chain Verity", "1302", "fcv"),
	FORCING_NET("Forcing Net", "xxxx", "fn"),
	FORCING_NET_CONTRADICTION("Forcing Net Contradiction", "1303", "fnc"),
	FORCING_NET_VERITY("Forcing Net Verity", "1304", "fnv"),
	BRUTE_FORCE("Brute Force", "xxxx", "bf"),
	INCOMPLETE("Incomplete Solution", "xxxx", "in"),
	GIVE_UP("Give Up", "xxxx", "gu"),
	GROUPED_NICE_LOOP("Grouped Nice Loop/AIC", "xxxx", "gnl"),
	GROUPED_CONTINUOUS_NICE_LOOP("Grouped Continuous Nice Loop", "0709", "gcnl"),
	GROUPED_DISCONTINUOUS_NICE_LOOP("Grouped Discontinuous Nice Loop", "0710", "gdnl"),
	EMPTY_RECTANGLE("Empty Rectangle", "0402", "er"),
	HIDDEN_RECTANGLE("Hidden Rectangle", "0606", "hr"),
	AVOIDABLE_RECTANGLE_1("Avoidable Rectangle Type 1", "0607", "ar1"),
	AVOIDABLE_RECTANGLE_2("Avoidable Rectangle Type 2", "0608", "ar2"),
	AIC("AIC", "0708", "aic"),
	GROUPED_AIC("Grouped AIC", "0711", "gaic"),
	SIMPLE_COLORS("Simple Colors", "xxxx", "sc"),
	MULTI_COLORS("Multi Colors", "xxxx", "mc"),
	KRAKEN_FISH("Kraken Fish", "xxxx", "kf"),
	TURBOT_FISH("Turbot Fish", "0403", "tf"),
	KRAKEN_FISH_TYPE_1("Kraken Fish Type 1", "0371", "kf1"),
	KRAKEN_FISH_TYPE_2("Kraken Fish Type 2", "0372", "kf2"),
	DUAL_TWO_STRING_KITE("Dual 2-String Kite", "0404", "d2sk"),
	DUAL_EMPTY_RECTANGLE("Dual Empty Rectangle", "0405", "der"),
	SIMPLE_COLORS_TRAP("Simple Colors Trap", "0500", "sc1"),
	SIMPLE_COLORS_WRAP("Simple Colors Wrap", "0501", "sc2"),
	MULTI_COLORS_1("Multi Colors 1", "0502", "mc1"),
	MULTI_COLORS_2("Multi Colors 2", "0503", "mc2");
	private String stepName;
	private String libraryType;
	private String argName;

	SolutionType() {
		// für XMLEncoder
	}

	SolutionType(String stepName, String libraryType, String argName) {
		this.setStepName(stepName);
		this.setLibraryType(libraryType);
		this.setArgName(argName);
	}

	@Override
	public String toString() {
		return "enum SolutionType: " + stepName + " (" + libraryType + "|" + argName + ")";
	}

	/**
	 * compareTo() is final and can't be overridden (uses ordinal() for comparison);
	 * Custom compare order: the order of steps matches the order configured in the
	 * solver; exception: fishes are sorted for (size, fin status, type) fin status:
	 * finned & sashimi are treated separately
	 * 
	 * @param t The SolutionType which should be compared with this
	 * @return &lt; 0 for this &lt; t, == 0 for this == t, &gt; 0 for this &gt; t
	 */
	public int compare(SolutionType t) {
		StepConfig s1 = getStepConfig();
		StepConfig s2 = getStepConfig(t);
		if (isFish() && t.isFish()) {
			SolutionCategory c1 = s1.getCategory();
			SolutionCategory c2 = s2.getCategory();
			if (c1.ordinal() != c2.ordinal()) {
				// different categories -> category defines order
				return c1.ordinal() - c2.ordinal();
			} else {
				// same category -> type.ordinal can be used
				// unfortunately not!
				// return ordinal() - t.ordinal();
				int size = getFishSize() - t.getFishSize();
				if (size != 0) {
					return size;
				}
				// same category and same size: check for Finned/Sashimi
				boolean sl = isSashimiFish();
				boolean sr = t.isSashimiFish();
				if (sl && sr || !sl && !sr) {
					// both are sashimi or both are not sashimi -> equal
					return 0;
				} else if (sl) {
					return 1;
				} else {
					return -1;
				}
			}
		}
		// for non-fishes use the sort order of the solver
		return s1.getIndex() - s2.getIndex();
	}

	public int getFishSize() {
		switch (this) {
		case X_WING:
		case FINNED_X_WING:
		case SASHIMI_X_WING:
		case FRANKEN_X_WING:
		case FINNED_FRANKEN_X_WING:
		case MUTANT_X_WING:
		case FINNED_MUTANT_X_WING:
			return 2;
		case SWORDFISH:
		case FINNED_SWORDFISH:
		case SASHIMI_SWORDFISH:
		case FRANKEN_SWORDFISH:
		case FINNED_FRANKEN_SWORDFISH:
		case MUTANT_SWORDFISH:
		case FINNED_MUTANT_SWORDFISH:
			return 3;
		case JELLYFISH:
		case FINNED_JELLYFISH:
		case SASHIMI_JELLYFISH:
		case FRANKEN_JELLYFISH:
		case FINNED_FRANKEN_JELLYFISH:
		case MUTANT_JELLYFISH:
		case FINNED_MUTANT_JELLYFISH:
			return 4;
		case SQUIRMBAG:
		case FINNED_SQUIRMBAG:
		case SASHIMI_SQUIRMBAG:
		case FRANKEN_SQUIRMBAG:
		case FINNED_FRANKEN_SQUIRMBAG:
		case MUTANT_SQUIRMBAG:
		case FINNED_MUTANT_SQUIRMBAG:
			return 5;
		case WHALE:
		case FINNED_WHALE:
		case SASHIMI_WHALE:
		case FRANKEN_WHALE:
		case FINNED_FRANKEN_WHALE:
		case MUTANT_WHALE:
		case FINNED_MUTANT_WHALE:
			return 6;
		default:
			return 7;
		}
	}

	public static boolean isSingle(SolutionType type) {
		if (type == HIDDEN_SINGLE || type == NAKED_SINGLE || type == FULL_HOUSE) {
			return true;
		}
		return false;
	}

	public boolean isSingle() {
		return isSingle(this);
	}

	public static boolean isSSTS(SolutionType type) {
		if (type.isSingle() || type == HIDDEN_PAIR || type == HIDDEN_TRIPLE || type == HIDDEN_QUADRUPLE
				|| type == NAKED_PAIR || type == NAKED_TRIPLE || type == NAKED_QUADRUPLE || type == LOCKED_PAIR
				|| type == LOCKED_TRIPLE || type == LOCKED_CANDIDATES || type == LOCKED_CANDIDATES_1
				|| type == LOCKED_CANDIDATES_2 || type == X_WING || type == SWORDFISH || type == JELLYFISH
				|| type == XY_WING || type == SIMPLE_COLORS || type == MULTI_COLORS) {
			return true;
		}
		return false;
	}

	public boolean isSSTS() {
		return isSSTS(this);
	}

	public static boolean isHiddenSubset(SolutionType type) {
		if (type.isSingle() || type == HIDDEN_PAIR || type == HIDDEN_TRIPLE || type == HIDDEN_QUADRUPLE) {
			return true;
		}
		return false;
	}

	public boolean isHiddenSubset() {
		return isHiddenSubset(this);
	}

	public StepConfig getStepConfig() {
		return getStepConfig(this);
	}

	/**
	 * Don't forget SolutionTypes tat don't have StepConfigs (e.g.
	 * DISCONTINUOUS_NICE_LOOP or DUAL_TWO_STRING_KITE)
	 * 
	 * @param type The SolutionType for which the StepConfig should be retrieved
	 * @return The StepConfig appropriate for type
	 */
	public static StepConfig getStepConfig(SolutionType type) {
		// Split Locked Candidates in two
//        if (type == SolutionType.LOCKED_CANDIDATES_1 || type == SolutionType.LOCKED_CANDIDATES_2) {
//            type = SolutionType.LOCKED_CANDIDATES;
//        }
		if (type == SolutionType.CONTINUOUS_NICE_LOOP || type == SolutionType.DISCONTINUOUS_NICE_LOOP
				|| type == SolutionType.AIC) {
			type = SolutionType.NICE_LOOP;
		}
		if (type == SolutionType.GROUPED_CONTINUOUS_NICE_LOOP || type == SolutionType.GROUPED_DISCONTINUOUS_NICE_LOOP
				|| type == SolutionType.GROUPED_AIC) {
			type = SolutionType.GROUPED_NICE_LOOP;
		}
		if (type == SolutionType.FORCING_CHAIN_CONTRADICTION || type == SolutionType.FORCING_CHAIN_VERITY) {
			type = SolutionType.FORCING_CHAIN;
		}
		if (type == SolutionType.FORCING_NET_CONTRADICTION || type == SolutionType.FORCING_NET_VERITY) {
			type = SolutionType.FORCING_NET;
		}
		if (type == SolutionType.KRAKEN_FISH_TYPE_1 || type == SolutionType.KRAKEN_FISH_TYPE_2) {
			type = SolutionType.KRAKEN_FISH;
		}
		if (type == SolutionType.DUAL_TWO_STRING_KITE) {
			type = SolutionType.TWO_STRING_KITE;
		}
		if (type == SolutionType.DUAL_EMPTY_RECTANGLE) {
			type = SolutionType.EMPTY_RECTANGLE;
		}
		if (type == SolutionType.SIMPLE_COLORS_TRAP || type == SolutionType.SIMPLE_COLORS_WRAP) {
			type = SolutionType.SIMPLE_COLORS;
		}
		if (type == SolutionType.MULTI_COLORS_1 || type == SolutionType.MULTI_COLORS_2) {
			type = SolutionType.MULTI_COLORS;
		}

		StepConfig[] configs = Options.getInstance().solverSteps;
		for (int i = 0; i < configs.length; i++) {
			if (configs[i].getType() == type) {
				return configs[i];
			}
		}
		return null;
	}

	public static boolean isFish(SolutionType type) {
		StepConfig config = getStepConfig(type);
		if (config != null) {
			if (config.getCategory() == SolutionCategory.BASIC_FISH
					|| config.getCategory() == SolutionCategory.FINNED_BASIC_FISH
					|| config.getCategory() == SolutionCategory.FRANKEN_FISH
					|| config.getCategory() == SolutionCategory.FINNED_FRANKEN_FISH
					|| config.getCategory() == SolutionCategory.MUTANT_FISH
					|| config.getCategory() == SolutionCategory.FINNED_MUTANT_FISH) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean isFish() {
		return isFish(this);
	}

	public static boolean isBasicFish(SolutionType type) {
		StepConfig config = getStepConfig(type);
		if (config != null) {
			if (config.getCategory() == SolutionCategory.BASIC_FISH
					|| config.getCategory() == SolutionCategory.FINNED_BASIC_FISH) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean isBasicFish() {
		return isBasicFish(this);
	}

	public static boolean isFrankenFish(SolutionType type) {
		StepConfig config = getStepConfig(type);
		if (config != null) {
			if (config.getCategory() == SolutionCategory.FRANKEN_FISH
					|| config.getCategory() == SolutionCategory.FINNED_FRANKEN_FISH) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean isFrankenFish() {
		return isFrankenFish(this);
	}

	public static boolean isMutantFish(SolutionType type) {
		StepConfig config = getStepConfig(type);
		if (config != null) {
			if (config.getCategory() == SolutionCategory.MUTANT_FISH
					|| config.getCategory() == SolutionCategory.FINNED_MUTANT_FISH) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean isMutantFish() {
		return isMutantFish(this);
	}

	public static boolean isKrakenFish(SolutionType type) {
		if (type == KRAKEN_FISH || type == KRAKEN_FISH_TYPE_1 || type == KRAKEN_FISH_TYPE_2) {
			return true;
		}
		return false;
	}

	public boolean isKrakenFish() {
		return isKrakenFish(this);
	}

	public static boolean isSashimiFish(SolutionType type) {
		if (type == SASHIMI_X_WING || type == SASHIMI_SWORDFISH || type == SASHIMI_JELLYFISH
				|| type == SASHIMI_SQUIRMBAG || type == SASHIMI_LEVIATHAN || type == SASHIMI_WHALE) {
			return true;
		}
		return false;
	}

	public boolean isSashimiFish() {
		return isSashimiFish(this);
	}

	public static boolean isSimpleChainOrLoop(SolutionType type) {
		if (type == NICE_LOOP || type == DISCONTINUOUS_NICE_LOOP || type == CONTINUOUS_NICE_LOOP
				|| type == GROUPED_NICE_LOOP || type == GROUPED_DISCONTINUOUS_NICE_LOOP
				|| type == GROUPED_CONTINUOUS_NICE_LOOP || type == X_CHAIN || type == XY_CHAIN || type == REMOTE_PAIR
				|| type == AIC || type == GROUPED_AIC) {
			return true;
		}
		return false;
	}

	public boolean isSimpleChainOrLoop() {
		return isSimpleChainOrLoop(this);
	}

	public static boolean useCandToDelInLibraryFormat(SolutionType type) {
		boolean ret = false;
		if (type == NICE_LOOP || type == CONTINUOUS_NICE_LOOP || type == DISCONTINUOUS_NICE_LOOP
				|| type == GROUPED_NICE_LOOP || type == GROUPED_CONTINUOUS_NICE_LOOP
				|| type == GROUPED_DISCONTINUOUS_NICE_LOOP || type == AIC || type == GROUPED_AIC
				|| type == FORCING_CHAIN_CONTRADICTION || type == FORCING_NET_CONTRADICTION || type == ALS_XZ
				|| type == ALS_XY_WING || type == ALS_XY_CHAIN || type == DEATH_BLOSSOM || type == SUE_DE_COQ) {
			ret = true;
		}
		return ret;
	}

	public boolean useCandToDelInLibraryFormat() {
		return useCandToDelInLibraryFormat(this);
	}

	public static int getNonSinglesAnz() {
		int anz = 0;
		for (SolutionType tmp : values()) {
			if (!tmp.isSingle()) {
				anz++;
			}
		}
		return anz;
	}

	public static int getNonSSTSAnz() {
		int anz = 0;
		for (SolutionType tmp : values()) {
			if (!tmp.isSingle() && !tmp.isSSTS()) {
				anz++;
			}
		}
		return anz;
	}

	public static SolutionType getTypeFromArgName(String argName) {
		for (int i = 0; i < values().length; i++) {
			if (argName.compareToIgnoreCase(values()[i].argName) == 0) {
				return values()[i];
			}
		}
		return null;
	}

	public static SolutionType getTypeFromLibraryType(String libraryType) {
		SolutionType ret = getTypeFromLibraryTypeInternal(libraryType);
		if (ret == null) {
			// could be a siamese fish: if the last character is '1' try without it
			if (libraryType.charAt(libraryType.length() - 1) == '1') {
				ret = getTypeFromLibraryTypeInternal(libraryType.substring(0, libraryType.length() - 1));
			}
		}
		return ret;
	}

	private static SolutionType getTypeFromLibraryTypeInternal(String libraryType) {
		for (int i = 0; i < values().length; i++) {
			if (libraryType.compareToIgnoreCase(values()[i].libraryType) == 0) {
				return values()[i];
			}
		}
		return null;
	}

	public String getLibraryType() {
		return libraryType;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public void setLibraryType(String libraryType) {
		this.libraryType = libraryType;
	}

	public String getArgName() {
		return argName;
	}

	public void setArgName(String argName) {
		this.argName = argName;
	}
}
