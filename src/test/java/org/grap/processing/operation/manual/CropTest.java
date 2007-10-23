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
package org.grap.processing.operation.manual;

import java.awt.Rectangle;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.operation.Crop;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class CropTest {
	public static void main(String[] args) throws Exception {
		String src = "../../datas2tests/grid/3x3.asc";

		GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();
		Rectangle rectangle = new Rectangle(1, 2, 1, 1);
		Operation crop = new Crop(rectangle);
		GeoRaster result = geoRaster.doOperation(crop);
		result.show();
		result.save("/tmp/1.tif");

		src = "../../datas2tests/geotif/440607.tif";
		geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();
		LinearRing polygon = new GeometryFactory()
				.createLinearRing(new Coordinate[] {
						new Coordinate(295895.3238300492, 2252680.3463808503),
						new Coordinate(295895.3238300492, 2252680.3463808503),
						new Coordinate(296907.69382697035, 2251783.230814348),
						new Coordinate(296907.69382697035, 2251783.230814348),
						new Coordinate(296272.2369673645, 2252181.9488439043),
						new Coordinate(295895.3238300492, 2252680.3463808503), });
		crop = new Crop(polygon);
		result = geoRaster.doOperation(crop);
		result.show();
	}
}