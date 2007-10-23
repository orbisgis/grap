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

import org.grap.io.GeoreferencingException;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

import com.vividsolutions.jts.geom.LinearRing;

public interface GeoRaster {
	public abstract void open() throws GeoreferencingException, IOException;

	public abstract RasterMetadata getMetadata();

	public abstract void setRangeValues(final double min, final double max)
			throws OperationException;

	public abstract void setRangeColors(final double[] ranges,
			final Color[] colors[]);

	public abstract void setNodataValue(final float value);

	public abstract Point2D pixelToWorldCoord(final int xpixel, final int ypixel);

	public abstract Point2D getPixelCoords(final double mouseX,
			final double mouseY);

	public abstract void save(final String dest) throws IOException;

	public abstract void show() throws IOException;

	public abstract void setLUT(final ColorModel colorModel) throws IOException;

	public abstract GeoRaster doOperation(final Operation operation)
			throws OperationException;

	/**
	 * @return ImagePlus.COLOR_256, ImagePlus.COLOR_RGB, ImagePlus.GRAY8,
	 *         ImagePlus.GRAY16, ImagePlus.GRAY32
	 * 
	 * @throws IOException
	 */
	public abstract int getType() throws IOException;

	public abstract boolean isEmpty();

	public abstract PixelProvider getPixelProvider() throws IOException;

	/**
	 * 
	 * @param roi
	 *            expressed in real world coordinates.
	 * @return
	 * @throws OperationException
	 */
	public abstract GeoRaster crop(LinearRing roi) throws OperationException;

	/**
	 * 
	 * @param roi
	 *            expressed in real world coordinates.
	 * @return
	 * @throws OperationException
	 */
	public abstract GeoRaster crop(Rectangle2D roi) throws OperationException;

	public abstract GeoRaster erode() throws OperationException;

	public abstract GeoRaster convolve(float[] kernel, int focalMeanSizeX,
			int focalMeanSizeY) throws OperationException;

	public abstract GeoRaster convolve3x3(int[] kernel)
			throws OperationException;

	public abstract GeoRaster smooth() throws OperationException;

	public abstract double getMin() throws IOException;

	public abstract double getMax() throws IOException;

	public abstract int getWidth() throws IOException;

	public abstract int getHeight() throws IOException;

	public abstract ColorModel getColorModel() throws IOException;
}