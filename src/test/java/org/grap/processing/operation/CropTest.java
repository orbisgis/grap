package org.grap.processing.operation;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.IOException;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.PixelProvider;
import org.grap.utilities.EnvelopeUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class CropTest extends GrapTest {
	private GeoRaster geoRasterSrc;

	private GeoRaster geoRasterDst;

	protected void setUp() throws Exception {
		super.setUp();
		geoRasterSrc = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/sample.asc");
		geoRasterSrc.open();
	}

	public void testCropPolygon() throws Exception {
		Envelope rasterEnvelope = geoRasterSrc.getMetadata().getEnvelope();
		int bufferSize = (int) (rasterEnvelope.getWidth() / 2.3);
		rasterEnvelope = new Envelope(new Coordinate(rasterEnvelope.getMinX()
				+ bufferSize, rasterEnvelope.getMinY() + bufferSize),
				new Coordinate(rasterEnvelope.getMaxX() - bufferSize,
						rasterEnvelope.getMaxY() - bufferSize));
		LinearRing polygon = (LinearRing) EnvelopeUtil
				.toGeometry(rasterEnvelope);
		geoRasterSrc.save("/tmp/1.tif");
		geoRasterDst = geoRasterSrc.doOperation(new Crop(polygon));
		geoRasterDst.save("/tmp/2.png");

		assertTrue(geoRasterDst.getWidth() > 0);
		assertTrue(geoRasterDst.getHeight() > 0);

		PixelProvider srcPixelProvider = geoRasterSrc.getPixelProvider();
		PixelProvider dstPixelProvider = geoRasterDst.getPixelProvider();
		checkCrop(geoRasterDst.getMetadata().getEnvelope(), srcPixelProvider,
				dstPixelProvider);
	}

	public void testCropPolygonOutside() throws Exception {
		LinearRing polygon = new GeometryFactory()
				.createLinearRing(new Coordinate[] { new Coordinate(100, 100),
						new Coordinate(100, 101), new Coordinate(101, 101),
						new Coordinate(101, 100), new Coordinate(100, 100) });
		geoRasterSrc.save("/tmp/1.tif");
		geoRasterDst = geoRasterSrc.doOperation(new Crop(polygon));

		assertTrue(geoRasterDst.isEmpty());
	}

	public void testCropRectangle() throws Exception {
		Envelope rasterEnvelope = geoRasterSrc.getMetadata().getEnvelope();
		geoRasterSrc.save("/tmp/1.tif");
		int buffer = (int) (rasterEnvelope.getWidth() / 2.3);
		Rectangle cropRectangle = new Rectangle((int) rasterEnvelope.getMinX()
				+ buffer, (int) rasterEnvelope.getMinY() + buffer,
				(int) rasterEnvelope.getWidth() - 2 * buffer,
				(int) rasterEnvelope.getHeight() - 2 * buffer);
		geoRasterDst = geoRasterSrc.doOperation(new Crop(cropRectangle));
		geoRasterDst.save("/tmp/2.tif");

		assertTrue(geoRasterDst.getWidth() > 0);
		assertTrue(geoRasterDst.getHeight() > 0);

		PixelProvider srcPixelProvider = geoRasterSrc.getPixelProvider();
		PixelProvider dstPixelProvider = geoRasterDst.getPixelProvider();
		checkCrop(geoRasterDst.getMetadata().getEnvelope(), srcPixelProvider,
				dstPixelProvider);
	}

	private void checkCrop(Envelope envelope, PixelProvider srcPixelProvider,
			PixelProvider dstPixelProvider) throws IOException {
		for (double x = envelope.getMinY(); x < envelope.getMaxY(); x++) {
			for (double y = envelope.getMinX(); y < envelope.getMaxX(); y++) {
				Point2D srcPixel = geoRasterSrc.getPixelCoords(x, y);
				Point2D dstPixel = geoRasterDst.getPixelCoords(x, y);
				float p = srcPixelProvider.getPixel((int) srcPixel.getX(),
						(int) srcPixel.getX());
				float p2 = dstPixelProvider.getPixel((int) dstPixel.getX(),
						(int) dstPixel.getX());
				if (Float.isNaN(p)) {
					assertTrue(Float.isNaN(p2));
				} else {
					assertTrue("pixel[" + x + ", " + y + "]", p == p2);
				}
			}
		}
	}

	public void testDontModifyOriginalRaster() throws Exception {
		GeoRaster gr = sampleRaster;
		byte[] pixels1 = gr.getPixelProvider().getBytePixels();
		GeoRaster gr2 = gr.convolve3x3(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		byte[] pixels2 = gr.getPixelProvider().getBytePixels();
		byte[] pixels3 = gr2.getPixelProvider().getBytePixels();
		assertTrue(equals(pixels1, pixels2));
		assertTrue(!equals(pixels3, pixels2));
	}

	private boolean equals(byte[] pixels1, byte[] pixels2) {
		for (int i = 0; i < pixels2.length; i++) {
			if (pixels1[i] != pixels2[i]) {
				return false;
			}
		}
		return pixels1.length == pixels2.length;
	}
}