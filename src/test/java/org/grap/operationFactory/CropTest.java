package org.grap.operationFactory;

import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class CropTest {

	/**
	 * @param args
	 * @throws NoninvertibleTransformException
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args)
			throws NoninvertibleTransformException, ParseException, IOException {

		// String fileName = "..//datas2tests//geotif//440607.tif";

		String fileName = "../..//datas2tests//grid//sample.asc";

		String dest = "../..//datas2tests//geotif//out440607.png";

		GeoRaster geoRaster = new GeoRaster(fileName);
		geoRaster.open();
		geoRaster.setRange(0, 1500);

		geoRaster.show();

		System.out.println(geoRaster.getImagePlus().getProcessor().getHeight());

		Geometry geom = new WKTReader()
				.read("POLYGON (( 295895.3238300492 2252680.3463808503, 295870.40395320195 2251805.0357065895, 296907.69382697035 2251783.230814348, 296817.35927339894 2252745.7610575743, 296272.2369673645 2252181.9488439043, 295895.3238300492 2252680.3463808503 ))");

		OperationFactory opf = new OperationFactory();
		
		Operation operation = opf
				.crop(geoRaster, new Rectangle(0, 0, 440, 440));

		GeoRaster result = operation.execute();

		result.getImagePlus().show();

		result.save(dest);

	}

}
