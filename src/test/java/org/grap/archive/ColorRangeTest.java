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
package org.grap.archive;

import ij.plugin.LutLoader;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class ColorRangeTest {

	public static void main(String[] args) throws Exception {

		LutLoader lutViewer = new LutLoader();

		lutViewer.run("/home/bocher/ImageJ/luts/erwan.lut");

		String src = "../../datas2tests/grid/mntzee_500.asc";

		GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);

		geoRaster.open();

		System.out.println("min " + geoRaster.getMin() + " max "
				+ geoRaster.getMax());

		float rang[] = new float[] { (float) geoRaster.getMin(), 400, 1000,
				(float) geoRaster.getMax() };
		byte[] red = new byte[] { (byte) 255, (byte) 128, (byte) 0xff };
		byte[] green = new byte[] { 0, 0, 0 };
		byte[] blue = new byte[] { 0, 0, 0 };

		ColorModel cm = new IndexColorModel(8, rang.length - 1, red, green,
				blue);

		// ColorModel cm = new IndexColorModel(8, 2, new byte[] { 0,
		// (byte) 255 }, new byte[] { 0, 0 }, new byte[] { (byte) 255,
		// 0 });

		geoRaster.setLUT(cm);

		geoRaster.show();

	}

}
