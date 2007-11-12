package org.grap.processing.hydrology;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.grap.model.GrapImagePlus;

public class SlopesComputations {
	private final static short[] neighboursDirection = new short[] { 16, 32,
			64, 128, 1, 2, 4, 8 };

	public static Set<Integer> fromCellSlopeDirectionIdxToContributiveArea(
			final GrapImagePlus gipSlopesDirections, final int ncols,
			final int nrows, final int cellIdx) throws IOException {
		final Set<Integer> contributiveArea = new HashSet<Integer>();
		final int[] neighboursIndices = new int[] { 1, ncols + 1, ncols,
				ncols - 1, -1, -ncols - 1, -ncols, -ncols + 1 };

		for (int i = 0; i < 8; i++) {
			final Integer tmp = getCellIndex(ncols, nrows, cellIdx
					+ neighboursIndices[i]);
			if (null != tmp) {
				final int rTmp = tmp / ncols;
				final int cTmp = tmp % ncols;
				if ((neighboursDirection[i] == gipSlopesDirections
						.getPixelValue(cTmp, rTmp))) {
					contributiveArea.add(tmp);
				}
			}
		}
		return contributiveArea;
	}

	public static Integer fromCellSlopeDirectionToNextCellIndex(
			final GrapImagePlus gipSlopesDirections, final int ncols,
			final int nrows, final int i) throws IOException {
		final int r = i / ncols;
		final int c = i % ncols;
		return fromCellSlopeDirectionToNextCellIndex(gipSlopesDirections,
				ncols, nrows, i, c, r);
	}

	public static Integer fromCellSlopeDirectionToNextCellIndex(
			final GrapImagePlus gipSlopesDirections, final int ncols,
			final int nrows, final int i, final int c, final int r)
			throws IOException {
		switch ((short) gipSlopesDirections.getPixelValue(c, r)) {
		case 1:
			return getCellIndex(ncols, nrows, i + 1, c + 1, r);
		case 2:
			return getCellIndex(ncols, nrows, i + ncols + 1, c + 1, r + 1);
		case 4:
			return getCellIndex(ncols, nrows, i + ncols, c, r + 1);
		case 8:
			return getCellIndex(ncols, nrows, i + ncols - 1, c - 1, r + 1);
		case 16:
			return getCellIndex(ncols, nrows, i - 1, c - 1, r);
		case 32:
			return getCellIndex(ncols, nrows, i - ncols - 1, c - 1, r - 1);
		case 64:
			return getCellIndex(ncols, nrows, i - ncols, c, r - 1);
		case 128:
			return getCellIndex(ncols, nrows, i - ncols + 1, c + 1, r - 1);
		}
		return null;
	}

	private static Integer getCellIndex(final int ncols, final int nrows,
			final int i) {
		final int r = i / ncols;
		final int c = i % ncols;
		return getCellIndex(ncols, nrows, i, c, r);
	}

	private static Integer getCellIndex(final int ncols, final int nrows,
			final int i, final int c, final int r) {
		return ((0 > r) || (nrows <= r) || (0 > c) || (ncols <= c)) ? null : i;
	}

	public static boolean isARiverStart(
			final GrapImagePlus gipSlopesAccumulations,
			final GrapImagePlus gipSlopesDirections, final int riverThreshold,
			final int ncols, final int nrows, final int i) throws IOException {
		final int r = i / ncols;
		final int c = i % ncols;
		final Float currAcc = gipSlopesAccumulations.getPixelValue(c, r);

		if (riverThreshold == currAcc) {
			return true;
		} else if (riverThreshold < currAcc) {
			final Set<Integer> contributiveArea = fromCellSlopeDirectionIdxToContributiveArea(
					gipSlopesDirections, ncols, nrows, i);
			for (int contributor : contributiveArea) {
				final int rContributor = contributor / ncols;
				final int cContributor = contributor % ncols;
				if (riverThreshold <= gipSlopesAccumulations.getPixelValue(
						cContributor, rContributor)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}