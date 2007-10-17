package org.grap.processing.cellularAutomata.cam;

public interface ICAFloat extends ICA {
	public float init(final int r, final int c, final int i);

	public float localTransition(final float[] rac, final int r, final int c,
			final int i);
}