package org.grap.processing.cellularAutomata;

public interface IShortCA extends ICA {
	public short init(final int r, final int c, final int i);

	public short localTransition(final short[] rac, final int r, final int c,
			final int i);
}