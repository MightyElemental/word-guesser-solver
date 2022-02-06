package tk.mightyelemental.world_solver;

import java.util.LinkedHashSet;
import java.util.Set;

public class LetterRestriction {

	/** The number of times the letter can occur */
	public int maxOccurrences = 5;

	/** The collection of indexes where the letter is known to be wrong */
	public Set<Integer> wrongPlacements;

	public LetterRestriction() {
		this(5);
	}

	public LetterRestriction( int maxOccur ) {
		wrongPlacements = new LinkedHashSet<Integer>();
		maxOccurrences = maxOccur;
	}

	/**
	 * Add an index at which the letter is known to be wrong
	 * 
	 * @param index the invalid index
	 * @return this object
	 */
	public LetterRestriction addWrongPlacement( int index ) {
		wrongPlacements.add(index);
		return this;
	}

	@Override
	public String toString() {
		// Don't display if it is the default
		if (maxOccurrences == 5) {
			return String.format("[wp=%s]", wrongPlacements);
		}
		return String.format("[max=%d, wp=%s]", maxOccurrences, wrongPlacements);
	}

}
