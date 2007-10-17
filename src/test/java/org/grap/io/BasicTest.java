package org.grap.io;

import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;

import org.grap.TestUtils;
import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.PixelProvider;
import org.grap.model.RasterMetadata;

public class BasicTest extends GrapTest {

	public void testGridWithoutHeader() throws Exception {
		try {
			GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
					+ "grid/ij3x3.asc");
			gr.open();
			assertTrue(false);
		} catch (GeoreferencingException e) {
		}
	}

	public void testPNGWithoutWorldFile() throws Exception {
		try {
			GeoRaster gr = GeoRasterFactory.createGeoRaster(internalData
					+ "noWorldFile.png");
			gr.open();
			gr.getType();
			assertTrue(false);
		} catch (GeoreferencingException e) {
		}
	}

	public void testSetNoDataValue() throws Exception {
		GeoRaster gr = sampleRaster;
		gr.open();
		gr.setNodataValue(0);
		gr.setNodataValue(1);
		boolean someZero = false;
		boolean someOne = false;
		PixelProvider pixelProvider = gr.getPixelProvider();
		for (int i = 0; i < gr.getWidth(); i++) {
			for (int j = 0; j < gr.getHeight(); j++) {
				if (pixelProvider.getPixel(i, j) == 0) {
					someZero = true;
				}
				if (pixelProvider.getPixel(i, j) == 1) {
					someOne = true;
				}
			}
		}

		someZero = false;
		someOne = false;
		pixelProvider = gr.getPixelProvider();
		byte[] pixel = pixelProvider.getBytePixels();
		for (int i = 0; i < pixel.length; i++) {
			if (pixel[i] == 0) {
				someZero = true;
			}
			if (pixel[i] == 1) {
				someOne = true;
			}
		}

		someZero = false;
		someOne = false;
		pixelProvider = gr.getPixelProvider();
		short[] shortPixel = pixelProvider.getShortPixels();
		for (int i = 0; i < shortPixel.length; i++) {
			if (shortPixel[i] == 0) {
				someZero = true;
			}
			if (shortPixel[i] == 1) {
				someOne = true;
			}
		}

		someZero = false;
		someOne = false;
		pixelProvider = gr.getPixelProvider();
		float[] floatPixel = pixelProvider.getFloatPixels();
		for (int i = 0; i < floatPixel.length; i++) {
			if (floatPixel[i] == 0) {
				someZero = true;
			}
			if (floatPixel[i] == 1) {
				someOne = true;
			}
		}

		assertTrue(someZero);
		assertFalse(someOne);
	}

	public void testSetLUT(GeoRaster gr) throws Exception {
		ColorModel originalCM = LutGenerator.colorModel("fire");
		gr.setLUT(originalCM);
		ColorModel cm = gr.getColorModel();
		int[] componentSize = cm.getComponentSize();
		for (int i = 0; i < componentSize.length; i++) {
			assertTrue(cm.getAlpha(i) == originalCM.getAlpha(i));
			assertTrue(cm.getRed(i) == originalCM.getRed(i));
			assertTrue(cm.getGreen(i) == originalCM.getGreen(i));
			assertTrue(cm.getBlue(i) == originalCM.getBlue(i));
		}
	}

	public void testSetLUT() throws Exception {
		testSetLUT(sampleRaster);
		testSetLUT(GeoRasterFactory.createGeoRaster(externalData
				+ "/geotif/440606.tif"));
		testSetLUT(GeoRasterFactory.createGeoRaster(externalData
				+ "/grid/sample.asc"));
	}

	public void testMemoryConsumption() throws Exception {
		final String[] src = new String[] {
				externalData + "/geotif/440606.tif",
				externalData + "/geotif/440607.tif",
				externalData + "/geotif/440608.tif",
				externalData + "/geotif/440706.tif",
				externalData + "/geotif/440707.tif",
				externalData + "/geotif/440708.tif",
				externalData + "/geotif/440806.tif",
				externalData + "/geotif/440807.tif",
				externalData + "/geotif/440808.tif", };

		GeoRaster[] rasters = new GeoRaster[src.length];

		for (int i = 0; i < src.length; i++) {
			TestUtils.printFreeMemory();
			rasters[i] = GeoRasterFactory.createGeoRaster(src[i]);
			TestUtils.printFreeMemory();
			rasters[i].open();
			TestUtils.printFreeMemory();
		}
	}

	public void testGrid2Tif() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/sample.asc");
		gr.open();
		RasterMetadata originalMetadata = gr.getMetadata();
		float[] pixels = gr.getPixelProvider().getFloatPixels();
		File file = new File("/tmp/1.tif");
		gr.save(file.getAbsolutePath());
		gr = GeoRasterFactory.createGeoRaster(file.getAbsolutePath());
		gr.open();
		float[] tifPixels = gr.getPixelProvider().getFloatPixels();
		assertTrue(tifPixels.length == pixels.length);
		equals(pixels, tifPixels);
		RasterMetadata newM = gr.getMetadata();
		assertTrue(newM.getEnvelope().equals(originalMetadata.getEnvelope()));
		assertTrue(newM.getNCols() == originalMetadata.getNCols());
		assertTrue(newM.getNRows() == originalMetadata.getNRows());
		assertTrue(newM.getPixelSize_X() == originalMetadata.getPixelSize_X());
		assertTrue(newM.getPixelSize_Y() == originalMetadata.getPixelSize_Y());
		assertTrue(newM.getRotation_X() == originalMetadata.getRotation_X());
		assertTrue(newM.getRotation_Y() == originalMetadata.getRotation_Y());
		assertTrue(newM.getXllcorner() == originalMetadata.getXllcorner());
		assertTrue(newM.getYllcorner() == originalMetadata.getYllcorner());
		assertTrue(newM.getXOrigin() == originalMetadata.getXOrigin());
		assertTrue(newM.getYOrigin() == originalMetadata.getYOrigin());
	}

	public void testLoadSaveGrid() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/3x3.asc");
		gr.open();
		check3x3(gr);
		gr.save("/tmp/1.png");

		gr = GeoRasterFactory.createGeoRaster("/tmp/1.png");
		gr.open();
		check3x3(gr);
	}

	private void check3x3(GeoRaster gr) throws IOException {
		PixelProvider pp = gr.getPixelProvider();
		float previous = -1;
		for (int y = 0; y < gr.getHeight(); y++) {
			for (int x = 0; x < gr.getWidth(); x++) {
				assertTrue(pp.getPixel(x, y) > previous);
				previous = pp.getPixel(x, y);
			}
		}
	}
}
