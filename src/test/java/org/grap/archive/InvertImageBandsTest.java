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
package org.grap.archive;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.TextReader;
import ij.process.ImageProcessor;

import org.grap.lut.LutGenerator;

public class InvertImageBandsTest {
	public static void main(String[] args) {
		final String src1 = "../../datas2tests/geotif/littlelehavre.tif";
		final Opener opener = new Opener();
		final ImagePlus imp1 = opener.openImage(src1);
		//imp1.getProcessor().setColorModel(LutGenerator.colorModel("fire"));
	//	imp1.show();
		
		
		ColorModel cm = imp1.getProcessor().getColorModel();
		
		 int rmask =  ((DirectColorModel) cm).getRedMask();
		 int gmask = ((DirectColorModel) cm).getGreenMask();
		 int bmask = ((DirectColorModel) cm).getBlueMask();
		
		System.out.println( cm.getNumColorComponents());
		
		DirectColorModel dcm = new DirectColorModel(24,  rmask, bmask, gmask);
		
		imp1.getProcessor().setColorModel(dcm);
		imp1.show();
		
		
	}
}