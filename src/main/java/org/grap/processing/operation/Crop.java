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

import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.process.ImageProcessor;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.utilities.EnvelopeUtil;
import org.grap.utilities.JTSConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class Crop implements Operation {
	private LinearRing ring;

	private Rectangle2D rectangle;

	/**
	 * @param ring
	 *            expressed in real world coordinates.
	 */
	public Crop(final LinearRing ring) {
		this.ring = ring;
	}

	/**
	 * @param rectangle
	 *            expressed in real world coordinates.
	 */
	public Crop(final Rectangle2D rectangle) {
		this.rectangle = rectangle;
	}

	public GeoRaster execute(final GeoRaster geoRaster)
			throws OperationException, GeoreferencingException {
		if (null != ring) {
			return execute(geoRaster, ring);
		} else if (null != rectangle) {
			return execute(geoRaster, rectangle);
		} else {
			throw new OperationException(
					"No cropped zone (polygon or rectangle) has been specified");
		}
	}

	private GeoRaster execute(final GeoRaster geoRaster, final LinearRing ring)
			throws OperationException, GeoreferencingException {
		try {
			final Geometry rasterEnvelope = new GeometryFactory()
					.createPolygon((LinearRing) EnvelopeUtil
							.toGeometry(geoRaster.getMetadata().getEnvelope()),
							null);

			if (rasterEnvelope.intersects(ring)) {
				final PolygonRoi roi = JTSConverter.toPolygonRoi(toPixel(
						geoRaster, ring));

				final ImageProcessor processor = geoRaster.getGrapImagePlus()
						.getProcessor();
				processor.setRoi(roi);
				final ImageProcessor result = processor.crop();

				final Envelope newEnvelope = toWorld(geoRaster, roi
						.getBoundingRect());

				final double originX = newEnvelope.getMinX();
				final double originY = newEnvelope.getMaxY();
				final RasterMetadata metadataResult = new RasterMetadata(
						originX, originY, geoRaster.getMetadata()
								.getPixelSize_X(), geoRaster.getMetadata()
								.getPixelSize_Y(), result.getWidth(), result
								.getHeight(), geoRaster.getMetadata()
								.getRotation_X(), geoRaster.getMetadata()
								.getRotation_Y());

				return createGeoRaster(geoRaster, result, metadataResult);
			} else {
				return GeoRasterFactory.createNullGeoRaster();
			}
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private GeoRaster execute(final GeoRaster geoRaster, final Rectangle2D roi)
			throws OperationException, GeoreferencingException {
		try {
			final Envelope roiEnv = new Envelope(new Coordinate(roi.getMinX(),
					roi.getMinY()),
					new Coordinate(roi.getMaxX(), roi.getMaxY()));
			if (roiEnv.intersects(geoRaster.getMetadata().getEnvelope())) {

				final Rectangle2D pixelRoi = toPixel(geoRaster, roi);
				final ImageProcessor processor = geoRaster.getGrapImagePlus()
						.getProcessor();
				processor.setRoi((int) pixelRoi.getMinX(), (int) pixelRoi
						.getMinY(), (int) pixelRoi.getWidth(), (int) pixelRoi
						.getHeight());
				final ImageProcessor result = processor.crop();

				final Envelope newEnvelope = toWorld(geoRaster, pixelRoi);
				final double originX = newEnvelope.getMinX();
				final double originY = newEnvelope.getMaxY();

				final RasterMetadata metadataResult = new RasterMetadata(
						originX, originY, geoRaster.getMetadata()
								.getPixelSize_X(), geoRaster.getMetadata()
								.getPixelSize_Y(), result.getWidth(), result
								.getHeight(), geoRaster.getMetadata()
								.getRotation_X(), geoRaster.getMetadata()
								.getRotation_Y());

				return createGeoRaster(geoRaster, result, metadataResult);
			} else {
				return GeoRasterFactory.createNullGeoRaster();
			}
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private GeoRaster createGeoRaster(final GeoRaster geoRaster,
			final ImageProcessor dup, final RasterMetadata rasterMetadata)
			throws IOException, GeoreferencingException {
		final int width = dup.getWidth();
		final int height = dup.getHeight();
		final ColorModel cm = dup.getColorModel();
		final int type = geoRaster.getType();
		switch (type) {
		case ImagePlus.GRAY8:
		case ImagePlus.COLOR_256:
			final byte[] bytePixels = (byte[]) dup.getPixels();
			return GeoRasterFactory.createGeoRaster(bytePixels, width, height,
					cm, rasterMetadata);
		case ImagePlus.GRAY16:
			final short[] shortPixels = (short[]) dup.getPixels();
			return GeoRasterFactory.createGeoRaster(shortPixels, width, height,
					cm, rasterMetadata);
		case ImagePlus.GRAY32:
		case ImagePlus.COLOR_RGB:
			final float[] floatPixels = (float[]) dup.getPixels();
			return GeoRasterFactory.createGeoRaster(floatPixels, width, height,
					cm, rasterMetadata);
		default:
			throw new IllegalStateException("Unknown type: " + type);
		}
	}

	private LinearRing toPixel(final GeoRaster geoRaster, final LinearRing ring) {
		final Coordinate[] coords = ring.getCoordinates();
		final Coordinate[] transformedCoords = new Coordinate[coords.length];
		for (int i = 0; i < transformedCoords.length; i++) {
			final Point2D p = geoRaster.fromRealWorldCoordToPixelGridCoord(
					coords[i].x, coords[i].y);
			transformedCoords[i] = new Coordinate(p.getX(), p.getY());
		}

		return new GeometryFactory().createLinearRing(transformedCoords);
	}

	private Rectangle2D toPixel(final GeoRaster geoRaster,
			final Rectangle2D rectangle) {
		// TODO following Math.ceil() must be validated !
		final Point2D min = geoRaster.fromRealWorldCoordToPixelGridCoord(
				rectangle.getMinX(), rectangle.getMinY());
		final Point2D max = geoRaster.fromRealWorldCoordToPixelGridCoord(
				rectangle.getMaxX(), rectangle.getMaxY());
		final int minx = (int) Math.min(min.getX(), max.getX());
		final int maxx = (int) Math
				.ceil(Math.max(min.getX(), max.getX()) + 0.5);
		final int miny = (int) Math.min(min.getY(), max.getY());
		final int maxy = (int) Math
				.ceil(Math.max(min.getY(), max.getY()) + 0.5);
		return new Rectangle(minx, miny, maxx - minx, maxy - miny);
	}

	private Envelope toWorld(final GeoRaster geoRaster, Rectangle2D rectangle) {
		// TODO following (int) cast must be validated !
		final Point2D min = geoRaster.fromPixelGridCoordToRealWorldCoord(
				(int) rectangle.getMinX(), (int) rectangle.getMinY());
		final Point2D max = geoRaster.fromPixelGridCoordToRealWorldCoord(
				(int) rectangle.getMaxX(), (int) rectangle.getMaxY());
		final double minx = Math.min(min.getX(), max.getX());
		final double maxx = Math.max(min.getX(), max.getX());
		final double miny = Math.min(min.getY(), max.getY());
		final double maxy = Math.max(min.getY(), max.getY());
		return new Envelope(new Coordinate(minx, miny), new Coordinate(maxx,
				maxy));
	}
}