package org.grap.processing.operation.manual;

import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.operation.Crop;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class CropTest {
	public static void main(String[] args)
			throws NoninvertibleTransformException, ParseException, IOException {
		String fileName = "../../datas2tests/grid/3x3.asc";

		GeoRaster geoRaster = GeoRasterFactory.read(fileName);
		Rectangle rectangle = new Rectangle(1, 2, 1, 1);
		Operation crop = new Crop(rectangle);
		GeoRaster result = geoRaster.doOperation(crop);
		result.show();
		result.save("/tmp/1.tif");

		fileName = "../../datas2tests/geotif/440607.tif";
		geoRaster = GeoRasterFactory.read(fileName);
		Polygon polygon = (Polygon) new WKTReader()
				.read("POLYGON ((295895.3238300492 2252680.3463808503,295870.40395320195 2251805.0357065895, 296907.69382697035 2251783.230814348, 296817.35927339894 2252745.7610575743,296272.2369673645 2252181.9488439043, 295895.3238300492 2252680.3463808503))");
		crop = new Crop(polygon);
		result = geoRaster.doOperation(crop);
		result.show();
	}
}