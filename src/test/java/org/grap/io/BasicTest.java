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
package org.grap.io;

import java.awt.image.ColorModel;
import java.io.File;

import org.grap.TestUtils;
import org.grap.lut.LutGenerator;
import org.grap.model.GeoProcessorType;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.GrapImagePlus;
import org.grap.model.RasterMetadata;

public class BasicTest extends GrapTest {

	public void testGridWithoutHeader() throws Exception {
		try {
			final GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
					+ "grid/ij3x3.asc");
			gr.open();
			assertTrue(false);
		} catch (GeoreferencingException e) {
		}
	}

	public void testXYZDEMReader() throws Exception {
		try {
			final GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
					+ "/xyz/MNT_Nantes_Lambert.xyz", GeoProcessorType.FLOAT,
					10);
			gr.open();
			gr.show();
			gr.save(tmpData + "xyzdem.tif");

		} catch (GeoreferencingException e) {
		}
	}

	public void testPNGWithoutWorldFile() throws Exception {
		try {
			final GeoRaster gr = GeoRasterFactory.createGeoRaster(internalData
					+ "noWorldFile.png");
			gr.open();
			gr.getType();
			assertTrue(false);
		} catch (GeoreferencingException e) {
		}
	}

	public void testSetNoDataValue() throws Exception {
		final GeoRaster gr = sampleRaster;
		gr.open();
		gr.setNodataValue(0);
		gr.setNodataValue(1);
		boolean someZero = false;
		boolean someOne = false;
		GrapImagePlus grapImagePlus = gr.getGrapImagePlus();
		for (int i = 0; i < gr.getWidth(); i++) {
			for (int j = 0; j < gr.getHeight(); j++) {
				if (grapImagePlus.getPixelValue(i, j) == 0) {
					someZero = true;
				}
				if (grapImagePlus.getPixelValue(i, j) == 1) {
					someOne = true;
				}
			}
		}

		someZero = false;
		someOne = false;
		grapImagePlus = gr.getGrapImagePlus();
		final byte[] pixel = grapImagePlus.getBytePixels();
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
		grapImagePlus = gr.getGrapImagePlus();
		final short[] shortPixel = grapImagePlus.getShortPixels();
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
		grapImagePlus = gr.getGrapImagePlus();
		final float[] floatPixel = grapImagePlus.getFloatPixels();
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
		final ColorModel originalCM = LutGenerator.colorModel("fire");
		gr.setLUT(originalCM);
		final ColorModel cm = gr.getColorModel();
		final int[] componentSize = cm.getComponentSize();
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

		final GeoRaster[] rasters = new GeoRaster[src.length];

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
		final RasterMetadata originalMetadata = gr.getMetadata();
		final float[] pixels = gr.getGrapImagePlus().getFloatPixels();
		final File file = new File(tmpData + "1.tif");
		gr.save(file.getAbsolutePath());
		gr = GeoRasterFactory.createGeoRaster(file.getAbsolutePath());
		gr.open();
		final float[] tifPixels = gr.getGrapImagePlus().getFloatPixels();
		assertTrue(tifPixels.length == pixels.length);
		equals(pixels, tifPixels);
		final RasterMetadata newM = gr.getMetadata();

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
		gr.save(tmpData + "1.png");

		gr = GeoRasterFactory.createGeoRaster(tmpData + "1.png");
		gr.open();
		check3x3(gr);
	}

	private void check3x3(GeoRaster gr) throws Exception {
		final GrapImagePlus grapImagePlus = gr.getGrapImagePlus();
		float previous = -1;
		for (int y = 0; y < gr.getHeight(); y++) {
			for (int x = 0; x < gr.getWidth(); x++) {
				assertTrue(grapImagePlus.getPixelValue(x, y) > previous);
				previous = grapImagePlus.getPixelValue(x, y);
			}
		}
	}
}