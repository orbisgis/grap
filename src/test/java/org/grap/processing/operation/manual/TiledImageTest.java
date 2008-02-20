package org.grap.processing.operation.manual;

import java.awt.Rectangle;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.operation.Crop;
import org.grap.utilities.EnvelopeUtil;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

public class TiledImageTest {

	
	public static void main(String[] args) throws Exception {
		
		Long start =  System.currentTimeMillis();
		String src = "../../datas2tests/geotif/leHavre.tif";
		GeoRaster geoRaster = GeoRasterFactory.createGeoRaster(src);	
		
		geoRaster.open();
		
		Envelope env = geoRaster.getMetadata().getEnvelope();
			
		
		
		LinearRing[] tiles = buildRectangleTiled(env);
		GeoRaster result = null;
		
		for (int i = 0; i < tiles.length; i++) {
			
			Crop crop = new Crop((LinearRing) tiles[i]);
			result = geoRaster.doOperation(crop);
			result.save("../../datas2tests/tmp/tiled "+ i +".tif");

		}
		
		
		System.out.println(System.currentTimeMillis() - start );

	}

	
		private static LinearRing[] buildRectangleTiled(Envelope env) {
		
		double width = env.getWidth();
		double heigth = env.getHeight();
		
		LinearRing[] linearRings = new LinearRing[4];
		
		//xm, xM, ym, yM
		
		double x1 = env.getMinX() + (width/2);
		double y1 = env.getMinY() + (heigth/2);
		
		
		
		Envelope en1 = new Envelope(env.getMinX(), x1, y1, env.getMaxY() );	
		Envelope en2 = new Envelope(x1, env.getMaxX(), y1, env.getMaxY());
		Envelope en3 = new Envelope(env.getMinX(), x1, env.getMinY() , y1 );
		Envelope en4 = new Envelope(x1, env.getMaxX(), env.getMinY(), y1);
		
		linearRings[0] = (LinearRing) EnvelopeUtil.toGeometry(en1);
		linearRings[1] = (LinearRing) EnvelopeUtil.toGeometry(en2);
		linearRings[2] = (LinearRing) EnvelopeUtil.toGeometry(en3);
		linearRings[3] = (LinearRing) EnvelopeUtil.toGeometry(en4);
		
		
		return linearRings;
		
		
		
		
	}
}
