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