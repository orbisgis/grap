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
package org.grap.processing.operation;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.GrapImagePlus;
import org.grap.model.RasterMetadata;
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
				.createLinearRing(new Coordinate[] {
						new Coordinate(100.5, 100.5),
						new Coordinate(100.5, 101.5),
						new Coordinate(101.5, 101.5),
						new Coordinate(101.5, 100.5),
						new Coordinate(100.5, 100.5) });
		geoRasterSrc.save(tmpData + "1.tif");
		geoRasterDst = geoRasterSrc.doOperation(new Crop(polygon));

		assertTrue(geoRasterDst.isEmpty());
	}

	public void testCropAll() throws Exception {
		final Envelope rasterEnvelope = geoRasterSrc.getMetadata()
				.getEnvelope();
		final Rectangle2D cropRectangle = new Rectangle2D.Double(rasterEnvelope
				.getMinX(), rasterEnvelope.getMinY(),
				rasterEnvelope.getWidth(), rasterEnvelope.getHeight());
		geoRasterDst = geoRasterSrc.doOperation(new Crop(cropRectangle));

		assertTrue(geoRasterDst.getWidth() > 0);
		assertTrue(geoRasterDst.getHeight() > 0);

		RasterMetadata dstMetadata = geoRasterDst.getMetadata();
		RasterMetadata srcMetadata = geoRasterSrc.getMetadata();
		assertTrue(dstMetadata.equals(srcMetadata));
	}

	public void testCropRectangle() throws Exception {
		final Envelope rasterEnvelope = geoRasterSrc.getMetadata()
				.getEnvelope();
		geoRasterSrc.save(tmpData + "1.tif");
		final int buffer = (int) (rasterEnvelope.getWidth() / 2.3);
		final Rectangle2D cropRectangle = new Rectangle2D.Double(rasterEnvelope
				.getMinX()
				+ buffer, rasterEnvelope.getMinY() + buffer, rasterEnvelope
				.getWidth()
				- 2 * buffer, rasterEnvelope.getHeight() - 2 * buffer);
		geoRasterDst = geoRasterSrc.doOperation(new Crop(cropRectangle));
		geoRasterDst.save(tmpData + "2.tif");

		assertTrue(geoRasterDst.getWidth() > 0);
		assertTrue(geoRasterDst.getHeight() > 0);

		final GrapImagePlus srcGrapImagePlus = geoRasterSrc.getGrapImagePlus();
		final GrapImagePlus dstGrapImagePlus = geoRasterDst.getGrapImagePlus();
		RasterMetadata dstMetadata = geoRasterDst.getMetadata();
		RasterMetadata srcMetadata = geoRasterSrc.getMetadata();
		assertTrue(dstMetadata.getEnvelope().getMinX() < cropRectangle
				.getMinX());
		assertTrue(dstMetadata.getEnvelope().getMinY() < cropRectangle
				.getMinY());
		assertTrue(dstMetadata.getEnvelope().getMaxX() > cropRectangle
				.getMaxX());
		assertTrue(dstMetadata.getEnvelope().getMaxY() > cropRectangle
				.getMaxY());
		assertTrue(dstMetadata.getEnvelope().getWidth() < srcMetadata
				.getEnvelope().getWidth());
		checkCrop(geoRasterDst.getMetadata().getEnvelope(), srcGrapImagePlus,
				dstGrapImagePlus);
	}

	private void checkCrop(Envelope envelope, GrapImagePlus srcPixelProvider,
			GrapImagePlus dstPixelProvider) throws IOException {
		// check metadata
		RasterMetadata dstMetadata = geoRasterDst.getMetadata();
		float sizeX = dstMetadata.getPixelSize_X();
		assertTrue(sizeX == geoRasterSrc.getMetadata().getPixelSize_X());
		int numPixelsX = dstMetadata.getNCols();
		assertTrue(numPixelsX * sizeX == dstMetadata.getEnvelope().getWidth());
		double xOrigin = dstMetadata.getXOrigin();
		assertTrue(xOrigin - (sizeX / 2) + numPixelsX * sizeX == dstMetadata
				.getEnvelope().getMaxX());

		// check raster values
		for (double x = envelope.getMinY(); x < envelope.getMaxY(); x = x + 1) {
			for (double y = envelope.getMinX(); y < envelope.getMaxX(); y = y + 1) {
				final Point2D srcPixel = geoRasterSrc.fromRealWorldCoordToPixelGridCoord(x, y);
				final Point2D dstPixel = geoRasterDst.fromRealWorldCoordToPixelGridCoord(x, y);
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

	

	private boolean equals(byte[] pixels1, byte[] pixels2) {
		for (int i = 0; i < pixels2.length; i++) {
			if (pixels1[i] != pixels2[i]) {
				return false;
			}
		}
		return pixels1.length == pixels2.length;
	}
}