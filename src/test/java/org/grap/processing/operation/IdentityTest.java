package org.grap.processing.operation;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;

public class IdentityTest extends GrapTest {
	public void testIdentity() throws Exception {
		// load the DEM
		final GeoRaster geoRasterSrc = GeoRasterFactory
				.createGeoRaster(externalData + "grid/smallsample.asc");
		geoRasterSrc.open();

		final Operation identity = new Identity();
		final GeoRaster identityResult = geoRasterSrc.doOperation(identity);
		float[] pixels = geoRasterSrc.getPixelProvider().getFloatPixels();
		identityResult.setNodataValue(0);
		float[] pixels2 = identityResult.getPixelProvider().getFloatPixels();
		assertTrue(equals(pixels, pixels2));
	}
}