package org.grap.processing.operation;

import ij.ImagePlus;
import ij.gui.PolygonRoi;

import java.awt.Rectangle;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.utilities.EnvelopeUtil;
import org.grap.utilities.JTSConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class Crop implements Operation {
	private Polygon polygon;

	private Rectangle rectangle;

	public Crop(final Polygon polygon) {
		this.polygon = polygon;
	}

	public Crop(final Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	public GeoRaster execute(final GeoRaster geoRaster) {
		final ImagePlus imp = geoRaster.getImagePlus();
		final RasterMetadata rasterMetadata = geoRaster.getMetadata();
		ImagePlus impResult = null;
		final RasterMetadata metadataResult = new RasterMetadata();

		if (null != polygon) {
			final Geometry geomEnvelope = EnvelopeUtil
					.toGeometry(rasterMetadata.getEnvelope());
			
			System.out.println(geomEnvelope);
			
			if (geomEnvelope.intersects(polygon)) {
				final PolygonRoi roi = JTSConverter.toPolygonRoi(geoRaster,
						(Polygon) polygon);

				imp.setRoi(roi);
				impResult = new ImagePlus("", imp.getProcessor().crop());
				final Envelope newEnvelope = geomEnvelope.intersection(polygon).getEnvelopeInternal();
				System.out.println(EnvelopeUtil
						.toGeometry(newEnvelope));
				metadataResult.setXOrigin(newEnvelope.getMinX());
				metadataResult.setYOrigin((newEnvelope.getMaxY()));
			}
		} else if (null != rectangle) {
			imp.setRoi(rectangle);
			impResult = new ImagePlus("", imp.getProcessor().crop());
			final Envelope newEnvelope = new Envelope(rectangle.getMinX(),
					rectangle.getMaxX(), rectangle.getMinY(), rectangle
							.getMaxY());
			final Coordinate coordinates = geoRaster.pixelToWorldCoord(
					(int) newEnvelope.getMinX(), (int) newEnvelope.getMaxY());
			metadataResult.setXOrigin(coordinates.x);
			metadataResult.setYOrigin(coordinates.y);
		}

		metadataResult.setPixelSize_X(rasterMetadata.getPixelSize_X());
		metadataResult.setPixelSize_Y(rasterMetadata.getPixelSize_Y());
		metadataResult.setXRotation(rasterMetadata.getRotation_X());
		metadataResult.setYRotation(rasterMetadata.getRotation_Y());
		metadataResult.setNCols(impResult.getWidth());
		metadataResult.setNRows(impResult.getHeight());

		return new GeoRaster(impResult, metadataResult);
	}
}