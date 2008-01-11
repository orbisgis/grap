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

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.io.IOException;

public class GrapImagePlus extends ImagePlus {
	private float noDataValue;
	private int type;

	// constructors
	// public GrapImagePlus() {
	// super();
	// }
	//
	// public GrapImagePlus(String pathOrURL) {
	// super(pathOrURL);
	// }
	//
	// public GrapImagePlus(String title, Image img) {
	// super(title, img);
	// }
	//
	public GrapImagePlus(String title, ImageProcessor ip) {
		super(title, ip);
	}

	//
	// public GrapImagePlus(String title, ImageStack stack) {
	// super(title, stack);
	// }

	// setters
	public void setNoDataValue(float noDataValue) {
		this.noDataValue = noDataValue;
	}

	public void setGrapType(int type) {
		this.type = type;
	}

	// public methods
	public float getPixelValue(int x, int y) throws IOException {
		final float f = getProcessor().getPixelValue(x, y);
		if (noDataValue == f) {
			return Float.NaN;
		} else {
			return f;
		}
	}

	public byte[] getBytePixels() throws IOException {
		byte[] result;

		switch (type) {
		case ImagePlus.GRAY8:
			result = (byte[]) getProcessor().getPixels();
			break;
		case ImagePlus.GRAY16:
		case ImagePlus.GRAY32:
		case ImagePlus.COLOR_256:
		case ImagePlus.COLOR_RGB:
			result = (byte[]) getProcessor().convertToByte(false).getPixels();
			break;
		default:
			throw new IllegalStateException("Unsupported raster type: " + type);
		}

		if (!Float.isNaN(noDataValue)) {
			for (int i = 0; i < result.length; i++) {
				if (result[i] == noDataValue) {
					result[i] = (byte) Float.NaN;
				}
			}
		}
		return result;
	}

	public short[] getShortPixels() throws IOException {
		short[] result;

		switch (type) {
		case ImagePlus.GRAY16:
			result = (short[]) getProcessor().getPixels();
			break;
		case ImagePlus.GRAY8:
		case ImagePlus.GRAY32:
		case ImagePlus.COLOR_256:
		case ImagePlus.COLOR_RGB:
			result = (short[]) getProcessor().convertToShort(false).getPixels();
			break;
		default:
			throw new IllegalStateException("Unsupported raster type: " + type);
		}

		if (!Float.isNaN(noDataValue)) {
			for (int i = 0; i < result.length; i++) {
				if (result[i] == noDataValue) {
					result[i] = (short) Float.NaN;
				}
			}
		}
		return result;
	}

	public float[] getFloatPixels() throws IOException {
		float[] result;
		switch (type) {
		case ImagePlus.GRAY32:
			result = (float[]) getProcessor().getPixels();
			break;
		case ImagePlus.GRAY8:
		case ImagePlus.GRAY16:
		case ImagePlus.COLOR_256:
		case ImagePlus.COLOR_RGB:
			result = (float[]) getProcessor().convertToFloat().getPixels();
			break;
		default:
			throw new IllegalStateException("Unsupported raster type: " + type);
		}

		if (!Float.isNaN(noDataValue)) {
			for (int i = 0; i < result.length; i++) {
				if (result[i] == noDataValue) {
					result[i] = Float.NaN;
				}
			}
		}
		return result;
	}

	public Object getPixels() throws IOException {
		switch (type) {
		case ImagePlus.GRAY8:
		case ImagePlus.COLOR_256:
			return getBytePixels();
		case ImagePlus.GRAY16:
			return getShortPixels();
		case ImagePlus.GRAY32:
		case ImagePlus.COLOR_RGB:
			return getFloatPixels();
		default:
			throw new IllegalStateException("Unsupported raster type: " + type);
		}
	}
}