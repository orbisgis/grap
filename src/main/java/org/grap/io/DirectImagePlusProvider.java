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
package org.grap.io;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;
import java.io.IOException;

public class DirectImagePlusProvider implements ImagePlusProvider {
	private FileReader fileReader;

	private CachedValues values = null;

	public DirectImagePlusProvider(final FileReader fileReader) {
		this.fileReader = fileReader;
	}

	private CachedValues getCachedValues(ImagePlus img) throws IOException {
		if (values == null) {
			values = new CachedValues();
			ImagePlus ip = img;
			if (ip == null) {
				ip = getImagePlus();
			}
			ImageProcessor processor = ip.getProcessor();
			values.colorModel = processor.getColorModel();
			values.min = processor.getMin();
			values.max = processor.getMax();
			values.height = ip.getHeight();
			values.width = ip.getWidth();
			values.type = ip.getType();

		}

		return values;
	}

	public ImagePlus getImagePlus() throws IOException {
		ImagePlus imagePlus;
		try {
			imagePlus = fileReader.readImagePlus();
		} catch (GeoreferencingException e) {
			throw new IOException(e.getMessage());
		}
		CachedValues cachedValues = getCachedValues(imagePlus);
		if (cachedValues.colorModel != null) {
			imagePlus.getProcessor().setColorModel(cachedValues.colorModel);
		}
		return imagePlus;
	}

	public void setLUT(final ColorModel colorModel) throws IOException {
		getCachedValues(null).colorModel = colorModel;
	}

	public ImageProcessor getProcessor() throws IOException {
		return getImagePlus().getProcessor();
	}

	public ColorModel getColorModel() throws IOException {
		return getCachedValues(null).colorModel;
	}

	public int getHeight() throws IOException {
		return getCachedValues(null).height;
	}

	public double getMax() throws IOException {
		return getCachedValues(null).max;
	}

	public double getMin() throws IOException {
		return getCachedValues(null).min;
	}

	public int getWidth() throws IOException {
		return getCachedValues(null).width;
	}

	public int getType() throws IOException {
		return getCachedValues(null).type;
	}

	private class CachedValues {

		private ColorModel colorModel;

		private int type;

		private double min;

		private double max;

		private int width;

		private int height;

	}
}