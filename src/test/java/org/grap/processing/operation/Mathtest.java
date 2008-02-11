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
package org.grap.processing.operation;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.operation.math.AbsValueOperation;
import org.grap.processing.operation.math.AddValueOperation;
import org.grap.processing.operation.math.DivideValueOperation;
import org.grap.processing.operation.math.MultiplyValueOperation;
import org.grap.processing.operation.math.SubtractValueOperation;

public class Mathtest extends GrapTest {
	private final static float EPSILON = 1E-4f;

	public void testAddValue() throws Exception {
		// load the DEM
		final GeoRaster geoRasterSrc = GeoRasterFactory
				.createGeoRaster(externalData + "grid/sample.asc");
		geoRasterSrc.open();

		int addedValue = 20;
		final Operation addValue = new AddValueOperation(addedValue);
		final GeoRaster gResult = geoRasterSrc.doOperation(addValue);

		float[] pixelsSrc = geoRasterSrc.getGrapImagePlus().getFloatPixels();

		float[] pixelsResult = gResult.getGrapImagePlus().getFloatPixels();

		for (int i = 0; i < pixelsResult.length; i++) {
			if (Float.isNaN(pixelsSrc[i])) {
				assertTrue(Float.isNaN(pixelsResult[i]));
			} else {
				assertTrue(Math.abs(pixelsSrc[i]
						- (pixelsResult[i] - addedValue)) < EPSILON);
			}
		}
	}
	
	public void testSubtractValue() throws Exception {
		// load the DEM
		final GeoRaster geoRasterSrc = GeoRasterFactory
				.createGeoRaster(externalData + "grid/sample.asc");
		geoRasterSrc.open();

		int addedValue = 20;
		final Operation addValue = new SubtractValueOperation(addedValue);
		final GeoRaster gResult = geoRasterSrc.doOperation(addValue);

		float[] pixelsSrc = geoRasterSrc.getGrapImagePlus().getFloatPixels();

		float[] pixelsResult = gResult.getGrapImagePlus().getFloatPixels();

		for (int i = 0; i < pixelsResult.length; i++) {
			if (Float.isNaN(pixelsSrc[i])) {
				assertTrue(Float.isNaN(pixelsResult[i]));
			} else {
				assertTrue(Math.abs(pixelsSrc[i]
						- (pixelsResult[i] + addedValue)) < EPSILON);
			}
		}
	}
	
	public void testMultiplyValue() throws Exception {
		// load the DEM
		final GeoRaster geoRasterSrc = GeoRasterFactory
				.createGeoRaster(externalData + "grid/sample.asc");
		geoRasterSrc.open();

		int addedValue = 20;
		final Operation addValue = new MultiplyValueOperation(addedValue);
		final GeoRaster gResult = geoRasterSrc.doOperation(addValue);

		float[] pixelsSrc = geoRasterSrc.getGrapImagePlus().getFloatPixels();

		float[] pixelsResult = gResult.getGrapImagePlus().getFloatPixels();

		for (int i = 0; i < pixelsResult.length; i++) {
			if (Float.isNaN(pixelsSrc[i])) {
				assertTrue(Float.isNaN(pixelsResult[i]));
			} else {
				assertTrue(Math.abs(pixelsSrc[i]
						- (pixelsResult[i] / addedValue)) < EPSILON);
			}
		}
	}
	
	public void testDivideValue() throws Exception {
		// load the DEM
		final GeoRaster geoRasterSrc = GeoRasterFactory
				.createGeoRaster(externalData + "grid/sample.asc");
		geoRasterSrc.open();

		int addedValue = 20;
		final Operation addValue = new DivideValueOperation(addedValue);
		final GeoRaster gResult = geoRasterSrc.doOperation(addValue);

		float[] pixelsSrc = geoRasterSrc.getGrapImagePlus().getFloatPixels();

		float[] pixelsResult = gResult.getGrapImagePlus().getFloatPixels();

		for (int i = 0; i < pixelsResult.length; i++) {
			if (Float.isNaN(pixelsSrc[i])) {
				assertTrue(Float.isNaN(pixelsResult[i]));
			} else {
				assertTrue(Math.abs(pixelsSrc[i]
						- (pixelsResult[i] * addedValue)) < EPSILON);
			}
		}
	}
	
	public void testABSValue() throws Exception {
		// load the DEM
		final GeoRaster geoRasterSrc = GeoRasterFactory
				.createGeoRaster(externalData + "grid/sample.asc");
		geoRasterSrc.open();

		int addedValue = 20;
		final Operation addValue = new AbsValueOperation();
		final GeoRaster gResult = geoRasterSrc.doOperation(addValue);

		
		float[] pixelsResult = gResult.getGrapImagePlus().getFloatPixels();

		for (int i = 0; i < pixelsResult.length; i++) {
			if (Float.isNaN(pixelsResult[i])) {
				assertTrue(Float.isNaN(pixelsResult[i]));
			} else {
				assertTrue(pixelsResult[i]>0);
			}
		}
	}
}