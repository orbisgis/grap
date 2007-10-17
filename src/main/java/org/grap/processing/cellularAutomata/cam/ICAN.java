package org.grap.processing.cellularAutomata.cam;

public interface ICAN {
	/* getter */
	public abstract Object getCANValues();

	/* public methods */
	public abstract int getStableState();

	public abstract void print(final String title);
}