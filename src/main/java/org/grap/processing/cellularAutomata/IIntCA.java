package org.grap.processing.cellularAutomata;

public interface IIntCA extends ICA {
	public int init(final int r, final int c, final int i);

	public int localTransition(final int[] rac, final int r, final int c,
			final int i);
}