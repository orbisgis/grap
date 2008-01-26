package org.grap.utilities;

import java.awt.geom.Point2D;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

import com.sun.swing.internal.plaf.metal.resources.metal;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

public class CoordinatesTest extends GrapTest{

	public void testWorldToPixelToWorld() throws Exception {
		
		String src = "../../datas2tests/geotif/440607.tif";
		GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);
		geoRaster.open();
		
		
		WKTReader wReader = new WKTReader();
		Point pointSource = (Point) wReader.read("POINT ( 296377.24441833847 2252204.828141853 )");
		
		RasterMetadata rasterMetadata = geoRaster.getMetadata();
		
		Point2D pixelCoords = rasterMetadata.toPixel(pointSource.getX(), pointSource.getY());
		
		Point2D toworld = rasterMetadata.toWorld((int)pixelCoords.getX(), (int)pixelCoords.getY());
		
		if (toworld.equals(pointSource)) {
			assertTrue(true);
		}
		else {
			assertTrue(false);
		}
		
		
	}
}
