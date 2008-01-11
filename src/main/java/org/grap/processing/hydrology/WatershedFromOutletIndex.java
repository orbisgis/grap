/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GRAP.
 *
 * GRAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAP. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GRAP.
 *
 * GRAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAP. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.grap.processing.hydrology;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.GrapImagePlus;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class WatershedFromOutletIndex implements Operation {
	public final static byte noDataValue = 0;
	public final static byte belongsToTheWatershed = 1;

	private GrapImagePlus ppSlopesDirections;
	private byte[] sameWatershed;
	private int ncols;
	private int nrows;

	private int outletIdx;

	public WatershedFromOutletIndex(final int outletIdx) {
		this.outletIdx = outletIdx;
	}

	public GeoRaster execute(final GeoRaster grSlopesDirections)
			throws OperationException, GeoreferencingException {
		try {
			final long startTime = System.currentTimeMillis();
			ppSlopesDirections = grSlopesDirections.getGrapImagePlus();
			final RasterMetadata rasterMetadata = grSlopesDirections
					.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			computeSameWatershed();
			final GeoRaster grAllOutlets = GeoRasterFactory.createGeoRaster(
					sameWatershed, ncols, nrows, rasterMetadata);
			grAllOutlets.setNodataValue(noDataValue);
			System.out.printf("Watershed for (%d,%d) in %d ms\n", outletIdx
					% ncols, outletIdx / ncols, System.currentTimeMillis()
					- startTime);
			return grAllOutlets;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private void computeSameWatershed() throws IOException {
		sameWatershed = new byte[nrows * ncols];
		Set<Integer> parentsCell = new HashSet<Integer>();
		parentsCell.add(outletIdx);
		do {
			parentsCell = computeSameWatershed(parentsCell);
		} while (0 < parentsCell.size());
	}

	private Set<Integer> computeSameWatershed(final Set<Integer> sonsCell)
			throws IOException {
		final Set<Integer> parentsCell = new HashSet<Integer>();
		for (int sonIdx : sonsCell) {
			sameWatershed[sonIdx] = belongsToTheWatershed;
			parentsCell.addAll(SlopesComputations
					.fromCellSlopeDirectionIdxToContributiveArea(
							ppSlopesDirections, ncols, nrows, sonIdx));
		}
		return parentsCell;
	}
}