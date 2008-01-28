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

import java.awt.Rectangle;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.operation.Crop;
import org.grap.utilities.EnvelopeUtil;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.io.WKTReader;

public class CropTest {
	public static void main(String[] args) throws Exception {
		String src = "../../datas2tests/geotif/440607.tif";
		GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();

		WKTReader wkt = new WKTReader();
		Geometry polygon = wkt
				.read("LINEARRING ( 295895.3238300492 2251783.230814348, 296907.69382697035 2251783.230814348, 296907.69382697035 2252680.3463808503, 295895.3238300492 2252680.3463808503, 295895.3238300492 2251783.230814348 )");

		System.out.println("Cropping envelope : " + polygon.toText());

		geoRaster.getGrapImagePlus().setRoi(new Rectangle(1000, 1000));

		Crop crop = new Crop((LinearRing) polygon);
		GeoRaster result = geoRaster.doOperation(crop);
		result.show();
		result.save("../../datas2tests/tmp/crop_440607.tif");

		System.out.println("Result envelope : "
				+ EnvelopeUtil.toGeometry(result.getMetadata().getEnvelope()));

	}
}