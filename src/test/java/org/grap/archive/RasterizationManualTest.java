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
package org.grap.archive;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.others.RasteringMode;
import org.grap.processing.operation.others.Rasterization;



import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;


import ij.gui.Roi;


public class RasterizationManualTest {
	public static void main(String[] args) throws OperationException, FileNotFoundException, IOException, GeoreferencingException {
			
		String src = "../../datas2tests/geotif/440607.tif";
		GeoRaster geoRasterSrc = GeoRasterFactory.createGeoRaster(src);
		
		geoRasterSrc.open();
		
		
		roiManual(geoRasterSrc, RasteringMode.DRAW);
		
		roisManual(geoRasterSrc, RasteringMode.FILL);
		
		roiManualSameValue(geoRasterSrc, RasteringMode.DRAW);
		
	}
	
	
	private static void roiManualSameValue(GeoRaster geoRasterSrc, RasteringMode rasteringMode) {
		try {
			
			Roi[] rois = new Roi[1];
			rois[0] = new Roi(new Rectangle(1000, 1000));
			
			double value = 12;
			
			final Operation rasterizing = new Rasterization(rasteringMode, rois, value);
			
			
			final GeoRaster gResult = geoRasterSrc.doOperation(rasterizing);
			
			
			gResult.save("/tmp/rasterisationRoi.tif");
			
			
			gResult.show();
			
			} catch (GeoreferencingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (OperationException e) {
				e.printStackTrace();
			}
		
	}


	public static void roiManual(GeoRaster geoRasterSrc, RasteringMode rasteringMode ){
		
		try {
			
		Roi[] rois = new Roi[1];
		rois[0] = new Roi(new Rectangle(1000, 1000));
		
		double[] values = new double[1];
		values[0] = 12;
		
		final Operation rasterizing = new Rasterization(rasteringMode, rois, values);
		
		
		final GeoRaster gResult = geoRasterSrc.doOperation(rasterizing);
		
		
		gResult.save("/tmp/rasterisationRoi.tif");
		
		
		gResult.show();
		
		} catch (GeoreferencingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OperationException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
public static void roisManual(GeoRaster geoRasterSrc, RasteringMode rasteringMode ){
		
		try {
			
			Roi[] rois = new Roi[2];
			rois[0] = new Roi(new Rectangle(1000, 1000));
			rois[1] = new Roi(new Rectangle(1002, 1002, 100, 100));
			
			
			
			double[] values = new double[2];
			values[0] = 12;
			values[1] = 120;
			
		
			final Operation rasterizing = new Rasterization(rasteringMode, rois, values);
		
		
			final GeoRaster gResult = geoRasterSrc.doOperation(rasterizing);
		
			gResult.save("/tmp/rasterisationRois.tif");
		
		
			gResult.show();
		
		} catch (GeoreferencingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OperationException e) {
			e.printStackTrace();
		}
		
	}
	
	private static LineString toPixel(final GeoRaster geoRaster, final LineString lineString) {
		final Coordinate[] realWorldCoords = lineString.getCoordinates();
		final Coordinate[] pixelGridCoords = new Coordinate[realWorldCoords.length];
		for (int i = 0; i < pixelGridCoords.length; i++) {
			final Point2D p = geoRaster.fromRealWorldCoordToPixelGridCoord(
					realWorldCoords[i].x, realWorldCoords[i].y);
			pixelGridCoords[i] = new Coordinate(p.getX(), p.getY());
		}
		return new GeometryFactory().createLineString(pixelGridCoords);
	}
	
	private static MultiLineString toPixel(final GeoRaster geoRaster, final MultiLineString mls) {

		LineString[] lineStrings = new LineString[mls.getNumGeometries()];
		for (int k = 0; k < mls.getNumGeometries(); k++) {
			LineString ls = (LineString) mls.getGeometryN(k);
			
			lineStrings[k] = toPixel(geoRaster, ls);
		}
		return new GeometryFactory().createMultiLineString(lineStrings);
	}
		
}