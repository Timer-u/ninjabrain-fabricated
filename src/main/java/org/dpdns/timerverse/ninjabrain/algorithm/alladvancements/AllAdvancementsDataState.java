package org.dpdns.timerverse.ninjabrain.algorithm.alladvancements;

import java.util.ArrayList;
import java.util.List;

public class AllAdvancementsDataState {

	private final List<AllAdvancementsPosition> structures = new ArrayList<>();
	private boolean hasEnteredEnd = false;

	public void addStructure(AllAdvancementsPosition pos) {
		structures.add(pos);
	}

	public void removeStructure(int index) {
		if (index >= 0 && index < structures.size()) {
			structures.remove(index);
		}
	}

	public void setHasEnteredEnd(boolean entered) {
		this.hasEnteredEnd = entered;
	}

	public boolean hasEnteredEnd() {
		return hasEnteredEnd;
	}

	public List<AllAdvancementsPosition> getStructures() {
		return new ArrayList<>(structures);
	}

	public void clear() {
		structures.clear();
	}

}
