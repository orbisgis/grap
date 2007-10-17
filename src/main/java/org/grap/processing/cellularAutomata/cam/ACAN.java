package org.grap.processing.cellularAutomata.cam;

public abstract class ACAN implements ICAN {
	private ICA ca;

	private Object rac0;

	private Object rac1;

	private int nbCells;

	/* constructor */
	public ACAN(final ICA ca) {
		this.ca = ca;
		nbCells = ca.getNRows() * ca.getNCols();

		if (ca instanceof ICAShort) {
			rac0 = new short[nbCells];
			rac1 = new short[nbCells];
		} else if (ca instanceof ICAFloat) {
			rac0 = new float[nbCells];
			rac1 = new float[nbCells];
		}
	}

	/* getters */
	public ICA getCa() {
		return ca;
	}

	public Object getRac0() {
		return rac0;
	}

	public Object getRac1() {
		return rac1;
	}

	public Object getCANValues() {
		return rac0;
	}

	public int getNbCells() {
		return nbCells;
	}

	/* public methods */
	public abstract int getStableState();

	public void print(final String title) {
		System.out.println(title);
		if (ca instanceof ICAShort) {
			short[] _rac0 = (short[]) rac0;
			for (int r = 0; r < ca.getNRows(); r++) {
				for (int c = 0; c < ca.getNCols(); c++) {
					System.out.printf("%3d\t", _rac0[r * ca.getNCols() + c]);
				}
				System.out.println();
			}
		} else if (ca instanceof ICAFloat) {
			float[] _rac0 = (float[]) rac0;
			for (int r = 0; r < ca.getNRows(); r++) {
				for (int c = 0; c < ca.getNCols(); c++) {
					System.out.printf("%.1f\t", _rac0[r * ca.getNCols() + c]);
				}
				System.out.println();
			}
		}
	}
}