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
package org.grap.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.io.IOException;

import org.grap.processing.Operation;

import com.vividsolutions.jts.geom.LinearRing;

class NullGeoRaster implements GeoRaster {
	static GeoRaster instance = new NullGeoRaster();

	/*
	 * We don't want instances of this class to be created by the user
	 */
	private NullGeoRaster() {
	}

	public GeoRaster doOperation(Operation operation) {
		return instance;
	}

	public GrapImagePlus getGrapImagePlus() {
		return null;
	}

	public RasterMetadata getMetadata() {
		return null;
	}

	public Point2D getPixelCoords(double mouseX, double mouseY) {
		return null;
	}

	public int getType() {
		return 0;
	}

	public void open() {
	}

	public Point2D pixelToWorldCoord(int xpixel, int ypixel) {
		return null;
	}

	public void save(String dest) throws IOException {
	}

	public void setLUT(ColorModel LUTName) {
	}

	public void setNodataValue(float value) {
	}

	public void setRangeValues(double min, double max) {
	}

	public void setRangeColors(final double[] ranges, final Color[] colors) {
	}

	public void show() {
	}

	public boolean isEmpty() {
		return true;
	}

	public int getPixel(int x, int y) {
		return 0;
	}

	public GeoRaster convolve(float[] kernel, int focalMeanSizeX,
			int focalMeanSizeY) {
		return instance;
	}

	public GeoRaster convolve3x3(int[] kernel) {
		return instance;
	}

	public GeoRaster crop(Rectangle2D roi) {
		return instance;
	}

	public GeoRaster erode() {
		return instance;
	}

	public GeoRaster smooth() {
		return instance;
	}

	public GeoRaster crop(LinearRing roi) {
		return instance;
	}

	public ColorModel getColorModel() {
		return null;
	}

	public int getHeight() {
		return 0;
	}

	public double getMax() {
		return 0;
	}

	public double getMin() {
		return 0;
	}

	public int getWidth() {
		return 0;
	}
}