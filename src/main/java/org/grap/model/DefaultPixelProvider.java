/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
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

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.io.IOException;

public class DefaultPixelProvider implements PixelProvider {
	private ImageProcessor processor;
	private int type;
	private float noDataValue;
	private Image image;

	public DefaultPixelProvider(final Image image,
			final ImageProcessor processor, int type, float noDataValue) {
		this.image = image;
		this.processor = processor;
		this.type = type;
		this.noDataValue = noDataValue;
	}

	public short[] getShortPixels() throws IOException {
		short[] ret;
		if (type == ImagePlus.GRAY8) {
			ret = (short[]) processor.convertToShort(false).getPixels();
		} else if (type == ImagePlus.GRAY16) {
			ret = (short[]) processor.getPixels();
		} else if (type == ImagePlus.GRAY32) {
			ret = (short[]) processor.convertToShort(false).getPixels();
		} else if (type == ImagePlus.COLOR_256) {
			ret = (short[]) processor.convertToShort(false).getPixels();
		} else if (type == ImagePlus.COLOR_RGB) {
			ret = (short[]) processor.convertToShort(false).getPixels();
		} else {
			throw new IllegalStateException("Unsupported raster type: " + type);
		}
		if (noDataValue != Float.NaN) {
			for (int i = 0; i < ret.length; i++) {
				if (ret[i] == noDataValue) {
					ret[i] = (short) Float.NaN;
				}
			}
		}
		return ret;
	}

	public byte[] getBytePixels() throws IOException {
		byte[] ret;
		if (type == ImagePlus.GRAY8) {
			ret = (byte[]) processor.getPixels();
		} else if (type == ImagePlus.GRAY16) {
			ret = (byte[]) processor.convertToByte(false).getPixels();
		} else if (type == ImagePlus.GRAY32) {
			ret = (byte[]) processor.convertToByte(false).getPixels();
		} else if (type == ImagePlus.COLOR_256) {
			ret = (byte[]) processor.convertToByte(false).getPixels();
		} else if (type == ImagePlus.COLOR_RGB) {
			ret = (byte[]) processor.convertToByte(false).getPixels();
		} else {
			throw new IllegalStateException("Unsupported raster type: " + type);
		}
		if (noDataValue != Float.NaN) {
			for (int i = 0; i < ret.length; i++) {
				if (ret[i] == noDataValue) {
					ret[i] = (short) Float.NaN;
				}
			}
		}
		return ret;
	}

	public float getPixel(int x, int y) throws IOException {
		float f = processor.getPixelValue(x, y);
		if (f == noDataValue) {
			return Float.NaN;
		} else {
			return f;
		}
	}

	public Object getPixels() throws IOException {
		return processor.getPixels();
	}

	public float[] getFloatPixels() throws IOException {
		float[] ret;
		if (type == ImagePlus.GRAY8) {
			ret = (float[]) processor.convertToFloat().getPixels();
		} else if (type == ImagePlus.GRAY16) {
			ret = (float[]) processor.convertToFloat().getPixels();
		} else if (type == ImagePlus.GRAY32) {
			ret = (float[]) processor.getPixels();
		} else if (type == ImagePlus.COLOR_256) {
			ret = (float[]) processor.convertToFloat().getPixels();
			ret = (float[]) processor.getPixels();
		} else if (type == ImagePlus.COLOR_RGB) {
			ret = (float[]) processor.convertToFloat().getPixels();
		} else {
			throw new IllegalStateException("Unsupported raster type: " + type);
		}
		if (noDataValue != Float.NaN) {
			for (int i = 0; i < ret.length; i++) {
				if (ret[i] == noDataValue) {
					ret[i] = Float.NaN;
				}
			}
		}
		return ret;
	}

	public Image getImage() {
		return image;
	}
}