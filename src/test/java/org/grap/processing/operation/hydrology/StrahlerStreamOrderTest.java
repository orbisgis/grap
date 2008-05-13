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
package org.grap.processing.operation.hydrology;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.operation.hydrology.GridAccumulation;
import org.grap.processing.operation.hydrology.StrahlerStreamOrder;

public class StrahlerStreamOrderTest extends GrapTest {
	private RasterMetadata rasterMetadata = new RasterMetadata(0, 15, 1, -1,
			ncols, nrows);
	private short[][] slopesDirections = new short[4][];
	private short[][] strahlerStreamOrder = new short[4][];
	private static int nrows = 10;
	private static int ncols = 10;

	protected void setUp() throws Exception {
		super.setUp();

		slopesDirections[0] = new short[] {//
		2, 0, 0, 0, 0, 0, 0, 4, 0, 8,//
				0, 2, 0, 0, 0, 0, 0, 4, 8, 0,//
				0, 0, 2, 0, 0, 0, 0, 8, 16, 16,//
				1, 1, 1, 2, 0, 0, 8, 0, 0, 0,//
				0, 0, 0, 0, 2, 4, 0, 0, 0, 0,//
				0, 0, 0, 0, 1, 2, 0, 0, 0, 0,//
				0, 0, 0, 128, 0, 0, 2, 0, 0, 0,//
				1, 1, 128, 0, 0, 0, 0, 2, 0, 0,//
				0, 128, 64, 0, 0, 0, 0, 0, 2, 0,//
				128, 0, 64, 0, 0, 0, 0, 0, 0, 2,//
		};

		strahlerStreamOrder[0] = new short[] {//
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 1, 0, 0, 0, 0, 0, 1, 1, 0,//
				0, 0, 1, 0, 0, 0, 0, 2, 1, 0,//
				0, 1, 1, 2, 0, 0, 2, 0, 0, 0,//
				0, 0, 0, 0, 2, 2, 0, 0, 0, 0,//
				0, 0, 0, 0, 2, 3, 0, 0, 0, 0,//
				0, 0, 0, 2, 0, 0, 3, 0, 0, 0,//
				0, 1, 2, 0, 0, 0, 0, 3, 0, 0,//
				0, 1, 1, 0, 0, 0, 0, 0, 3, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 3,//
		};

		slopesDirections[1] = new short[] {//
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 64, 0, 0, 0, 0,//
				1, 1, 1, 1, 1, 64, 0, 0, 0, 0,//
				0, 0, 128, 0, 0, 64, 0, 0, 0, 0,//
				0, 128, 0, 0, 0, 64, 0, 0, 0, 0,//
				128, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
		};

		strahlerStreamOrder[1] = new short[] {//
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 2, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 2, 0, 0, 0, 0,//
				0, 1, 1, 2, 2, 2, 0, 0, 0, 0,//
				0, 0, 1, 0, 0, 1, 0, 0, 0, 0,//
				0, 1, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
		};

		slopesDirections[2] = new short[] {//
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 64, 0, 0, 0, 0,//
				1, 1, 1, 1, 1, 64, 0, 0, 0, 0,//
				0, 0, 128, 0, 0, 64, 0, 0, 0, 0,//
				0, 128, 0, 0, 0, 64, 0, 0, 0, 0,//
				128, 0, 0, 0, 128, 0, 32, 0, 0, 0,//
				0, 0, 0, 128, 0, 0, 0, 32, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
		};

		strahlerStreamOrder[2] = new short[] {//
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 3, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 3, 0, 0, 0, 0,//
				0, 1, 1, 2, 2, 3, 0, 0, 0, 0,//
				0, 0, 1, 0, 0, 2, 0, 0, 0, 0,//
				0, 1, 0, 0, 0, 2, 0, 0, 0, 0,//
				0, 0, 0, 0, 1, 0, 1, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
		};

		slopesDirections[3] = new short[] {//
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 64, 0, 0, 0, 0, 0,//
				0, 0, 0, 1, 64, 0, 0, 0, 0, 0,//
				0, 0, 128, 0, 64, 0, 0, 0, 0, 0,//
				0, 128, 0, 0, 64, 0, 0, 0, 0, 0,//
				128, 0, 0, 0, 64, 0, 0, 0, 0, 0,//
				0, 0, 0, 128, 0, 0, 0, 0, 0, 0,//
				0, 0, 128, 0, 0, 0, 0, 0, 0, 0,//
		};

		strahlerStreamOrder[3] = new short[] {//
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 2, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 2, 0, 0, 0, 0, 0,//
				0, 0, 0, 1, 2, 0, 0, 0, 0, 0,//
				0, 0, 1, 0, 1, 0, 0, 0, 0, 0,//
				0, 1, 0, 0, 1, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 1, 0, 0, 0, 0, 0,//
				0, 0, 0, 1, 0, 0, 0, 0, 0, 0,//
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0,//
		};

	}

	public void testStrahlerStreamOrder() throws Exception {
		for (int i = 0; i < strahlerStreamOrder.length; i++) {
			final GeoRaster grSlopesDirections = GeoRasterFactory
					.createGeoRaster(slopesDirections[i], ncols, nrows,
							rasterMetadata);

			// compute the slopes accumulations
			final Operation slopesAccumulations = new GridAccumulation();
			final GeoRaster grSlopesAccumulations = grSlopesDirections
					.doOperation(slopesAccumulations);
			printGeoRasterAndArray(grSlopesAccumulations, slopesDirections[i]);

			// compute the Strahler stream orders
			final int riverThreshold = 1;
			final Operation opeStrahlerStreamOrder = new StrahlerStreamOrder(
					grSlopesAccumulations, riverThreshold);
			final GeoRaster grStrahlerStreamOrder = grSlopesDirections
					.doOperation(opeStrahlerStreamOrder);

			// compare the computed watersheds with previous ones
			grStrahlerStreamOrder.setNodataValue(1234.5678f);
			printGeoRasterAndArray(grStrahlerStreamOrder,
					strahlerStreamOrder[i]);
			compareGeoRasterAndArray(grStrahlerStreamOrder,
					strahlerStreamOrder[i]);
		}
	}
}