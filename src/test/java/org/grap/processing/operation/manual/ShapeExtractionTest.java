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
package org.grap.processing.operation.manual;

import ij.gui.Roi;
import ij.gui.Wand;

import java.awt.Color;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class ShapeExtractionTest {
	public static void main(String[] args) throws Exception {
		String src = "../../datas2tests/grid/test.tif";

		final GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();
		
		Wand w = new Wand(geoRaster.getGrapImagePlus().getProcessor());
		
		w.autoOutline(150, 150);
		
		System.out.println("Points:" + w.npoints);
		
		int x[] = w.xpoints ;
        int y[] = w.ypoints  ;
        Roi roi = new ij.gui.PolygonRoi(x, y, w.npoints,geoRaster.getGrapImagePlus(),
        		Roi.POLYGON   );
        
        
        
        geoRaster.getGrapImagePlus().setRoi(roi);
        
        
		
		geoRaster.show();
	}
}