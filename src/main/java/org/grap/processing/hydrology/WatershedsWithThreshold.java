/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
import java.util.HashMap;
import java.util.Map;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.GrapImagePlus;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class WatershedsWithThreshold implements Operation {
	public final static short noDataValue = (short) Float.NaN;

	private GrapImagePlus gipAllWatersheds;
	private GrapImagePlus gipAllOutlets;
	private GrapImagePlus gipSlopesAccumulations;
	private short[] watershedsWithThreshold;
	private int threshold;
	private int ncols;
	private int nrows;

	public WatershedsWithThreshold(final GeoRaster grAllWatersheds,
			final GeoRaster grAllOutlets, final int threshold)
			throws OperationException, GeoreferencingException {
		try {
			gipAllWatersheds = grAllWatersheds.getGrapImagePlus();
			gipAllOutlets = grAllOutlets.getGrapImagePlus();
		} catch (IOException e) {
			throw new OperationException(e);
		}
		this.threshold = threshold;
	}

	public GeoRaster execute(final GeoRaster grSlopesAccumulations)
			throws OperationException, GeoreferencingException {
		try {
			final long startTime = System.currentTimeMillis();
			gipSlopesAccumulations = grSlopesAccumulations.getGrapImagePlus();
			final RasterMetadata rasterMetadata = grSlopesAccumulations
					.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			int nbOfWatershedsWithThreshold = computeAllwatershedsWithThreshold();
			final GeoRaster grWatershedsWithThreshold = GeoRasterFactory
					.createGeoRaster(watershedsWithThreshold, ncols, nrows,
							rasterMetadata);
			grWatershedsWithThreshold.setNodataValue(noDataValue);
			System.out.printf(
					"%d watersheds (outlet's threshold = %d) in %d ms\n",
					nbOfWatershedsWithThreshold, threshold, System
							.currentTimeMillis()
							- startTime);
			return grWatershedsWithThreshold;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private int computeAllwatershedsWithThreshold() throws IOException {
		short nbOfWatershedsWithThreshold = 0;
		final Map<Float, Short> mapOfBigOutlets = new HashMap<Float, Short>();

		// 1st step: identify the "good" outlets...
		int i = 0;
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++, i++) {
				if ((!Float.isNaN(gipAllOutlets.getPixelValue(c, r)))
						&& (gipSlopesAccumulations.getPixelValue(c, r) >= threshold)) {
					// current cell is an outlet. It's slopes accumulation value
					// is greater or equal to the threshold value.
					System.out.printf("(%d, %d) : %.0f\n", c, r,
							gipSlopesAccumulations.getPixelValue(c, r));
					nbOfWatershedsWithThreshold++;
					mapOfBigOutlets.put(gipAllWatersheds.getPixelValue(c, r),
							nbOfWatershedsWithThreshold);
				}
			}
		}
		// 2nd step:
		watershedsWithThreshold = new short[nrows * ncols];
		i = 0;
		for (int r = 0; r < nrows; r++) {
			for (int c = 0; c < ncols; c++, i++) {
				final float tmp = gipAllWatersheds.getPixelValue(c, r);
				watershedsWithThreshold[i] = mapOfBigOutlets.containsKey(tmp) ? mapOfBigOutlets
						.get(tmp)
						: noDataValue;
			}
		}
		return nbOfWatershedsWithThreshold;
	}
}