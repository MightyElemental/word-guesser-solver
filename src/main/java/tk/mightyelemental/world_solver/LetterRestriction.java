package tk.mightyelemental.world_solver;

import java.util.LinkedHashSet;
import java.util.Set;

public class LetterRestriction {

	/** Whether or not the letter can occur multiple times */
	public boolean multipleOccurrences = true;

	/** The collection of indexes where the letter is known to be wrong */
	public Set<Integer> wrongPlacements;

	public LetterRestriction() {
		this(true);
	}

	public LetterRestriction( boolean multipleOccur ) {
		wrongPlacements = new LinkedHashSet<Integer>();
		multipleOccurrences = multipleOccur;
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
		if (!multipleOccurrences) {
			return String.format("[wp=%s]", wrongPlacements);
		}
		return String.format("[pm, wp=%s]", wrongPlacements);
	}

}
