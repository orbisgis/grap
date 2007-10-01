package org.grap.processing.operation;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import junit.framework.TestCase;

import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

public class CropTest extends TestCase {
	private GeoRaster geoRasterSrc;

	private GeoRaster geoRasterDst;

	protected void setUp() throws Exception {
		super.setUp();

		final int nrows = 10;
		final int ncols = 10;
		final byte[] values = new byte[nrows * ncols];
		for (int i = 0; i < nrows * ncols; i++) {
			values[i] = (byte) i;
		}

		final ImagePlus imp = new ImagePlus("", new ByteProcessor(ncols, nrows,
				values, LutGenerator.colorModel("fire")));
		final RasterMetadata rmd = new RasterMetadata();
		rmd.setNRows(nrows);
		rmd.setNCols(ncols);
		geoRasterSrc = new GeoRaster(imp, rmd);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCropPolygon() throws Exception {
		final String[] polygons = new String[] {
				"POLYGON((0 0 , 0 -10, 10 -10, 10 0, 0 0))",
				"POLYGON((0 0 , 0 10, 10 10, 10 0, 0 0))",
				"POLYGON((1 5, 9 5, 9 7, 1 7, 1 5))" };
		for (String item : polygons) {
			final Polygon p = (Polygon) new WKTReader().read(item);
			System.err.println("\n" + p);
			if (!testCropPolygonAux(p)) {
				fail();
			}
		}
	}

	private boolean testCropPolygonAux(final Polygon polygon) {
		geoRasterDst = geoRasterSrc.doOperation(new Crop(polygon));
		int i = 0;
		for (int r = 0; r < geoRasterDst.getMetadata().getNRows(); r++) {
			for (int c = 0; c < geoRasterDst.getMetadata().getNCols(); c++) {
				// System.out.printf("%d %d %d %f\n", r, c, i, geoRasterDst
				// .getImagePlus().getProcessor().getPixelValue(c, r));
				if (i != geoRasterDst.getImagePlus().getProcessor()
						.getPixelValue(c, r)) {
					return false;
					// fail("pixel[" + r + ", " + c + "] != " + i);
				}
				i++;
			}
		}
		System.err.printf("geoRasterDst : %d x %d\n", geoRasterDst
				.getMetadata().getNRows(), geoRasterDst.getMetadata()
				.getNCols());
		return true;
	}

	public void testCropRectangle() {
		// fail("Not yet implemented");
	}

	public void testExecute() {
		// fail("Not yet implemented");
	}
}