/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
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
package org.grap.processing.operation;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.IOException;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.GrapImagePlus;
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
		final int bufferSize = (int) (rasterEnvelope.getWidth() / 2.3);
		rasterEnvelope = new Envelope(new Coordinate(rasterEnvelope.getMinX()
				+ bufferSize, rasterEnvelope.getMinY() + bufferSize),
				new Coordinate(rasterEnvelope.getMaxX() - bufferSize,
						rasterEnvelope.getMaxY() - bufferSize));
		final LinearRing polygon = (LinearRing) EnvelopeUtil
				.toGeometry(rasterEnvelope);
		geoRasterSrc.save(tmpData + "1.tif");
		geoRasterDst = geoRasterSrc.doOperation(new Crop(polygon));
		geoRasterDst.save(tmpData + "2.png");

		assertTrue(geoRasterDst.getWidth() > 0);
		assertTrue(geoRasterDst.getHeight() > 0);

		final GrapImagePlus srcGrapImagePlus = geoRasterSrc.getGrapImagePlus();
		final GrapImagePlus dstGrapImagePlus = geoRasterDst.getGrapImagePlus();
		checkCrop(geoRasterDst.getMetadata().getEnvelope(), srcGrapImagePlus,
				dstGrapImagePlus);
	}

	public void testCropPolygonOutside() throws Exception {
		final LinearRing polygon = new GeometryFactory()
				.createLinearRing(new Coordinate[] { new Coordinate(100, 100),
						new Coordinate(100, 101), new Coordinate(101, 101),
						new Coordinate(101, 100), new Coordinate(100, 100) });
		geoRasterSrc.save(tmpData + "1.tif");
		geoRasterDst = geoRasterSrc.doOperation(new Crop(polygon));

		assertTrue(geoRasterDst.isEmpty());
	}

	public void testCropRectangle() throws Exception {
		final Envelope rasterEnvelope = geoRasterSrc.getMetadata()
				.getEnvelope();
		geoRasterSrc.save(tmpData + "1.tif");
		final int buffer = (int) (rasterEnvelope.getWidth() / 2.3);
		final Rectangle cropRectangle = new Rectangle((int) rasterEnvelope
				.getMinX()
				+ buffer, (int) rasterEnvelope.getMinY() + buffer,
				(int) rasterEnvelope.getWidth() - 2 * buffer,
				(int) rasterEnvelope.getHeight() - 2 * buffer);
		geoRasterDst = geoRasterSrc.doOperation(new Crop(cropRectangle));
		geoRasterDst.save(tmpData + "2.tif");

		assertTrue(geoRasterDst.getWidth() > 0);
		assertTrue(geoRasterDst.getHeight() > 0);

		final GrapImagePlus srcGrapImagePlus = geoRasterSrc.getGrapImagePlus();
		final GrapImagePlus dstGrapImagePlus = geoRasterDst.getGrapImagePlus();
		checkCrop(geoRasterDst.getMetadata().getEnvelope(), srcGrapImagePlus,
				dstGrapImagePlus);
	}

	private void checkCrop(Envelope envelope, GrapImagePlus srcPixelProvider,
			GrapImagePlus dstPixelProvider) throws IOException {
		for (double x = envelope.getMinY(); x < envelope.getMaxY(); x++) {
			for (double y = envelope.getMinX(); y < envelope.getMaxX(); y++) {
				final Point2D srcPixel = geoRasterSrc.getPixelCoords(x, y);
				final Point2D dstPixel = geoRasterDst.getPixelCoords(x, y);
				final float p = srcPixelProvider.getPixelValue((int) srcPixel
						.getX(), (int) srcPixel.getX());
				final float p2 = dstPixelProvider.getPixelValue((int) dstPixel
						.getX(), (int) dstPixel.getX());
				if (Float.isNaN(p)) {
					assertTrue(Float.isNaN(p2));
				} else {
					assertTrue("pixel[" + x + ", " + y + "]", p == p2);
				}
			}
		}
	}

	public void testDontModifyOriginalRaster() throws Exception {
		final GeoRaster gr = sampleRaster;
		final byte[] pixels1 = gr.getGrapImagePlus().getBytePixels();
		final GeoRaster gr2 = gr.convolve3x3(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		final byte[] pixels2 = gr.getGrapImagePlus().getBytePixels();
		final byte[] pixels3 = gr2.getGrapImagePlus().getBytePixels();
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