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
package org.grap.processing.operation.others;

import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.GrapImagePlus;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;

public class Shadows implements Operation {
	private Orientations orientation;

	public Shadows(final Orientations orientation) {
		this.orientation = orientation;
	}

	public GeoRaster execute(final GeoRaster geoRaster)
			throws OperationException, GeoreferencingException {
		try {
			geoRaster.open();
			final GrapImagePlus imagePlus = geoRaster.getGrapImagePlus();

			switch (orientation) {
			case NORTH:
				imagePlus.getProcessor().convolve3x3(
						new int[] { 1, 2, 1, 0, 1, 0, -1, -2, -1 });
				return GeoRasterFactory.createGeoRaster(imagePlus, geoRaster
						.getMetadata());
			case SOUTH:
				imagePlus.getProcessor().convolve3x3(
						new int[] { -1, -2, -1, 0, 1, 0, 1, 2, 1 });
				return GeoRasterFactory.createGeoRaster(imagePlus, geoRaster
						.getMetadata());

			case EAST:
				imagePlus.getProcessor().convolve3x3(
						new int[] { -1, 0, 1, -2, 1, 2, -1, 0, 1 });
				return GeoRasterFactory.createGeoRaster(imagePlus, geoRaster
						.getMetadata());
			case WEST:
				imagePlus.getProcessor().convolve3x3(
						new int[] { 1, 0, -1, 2, 1, -2, 1, 0, -1 });
				return GeoRasterFactory.createGeoRaster(imagePlus, geoRaster
						.getMetadata());
			case NORTHWEST:
				imagePlus.getProcessor().convolve3x3(
						new int[] { 2, 1, 0, 1, 1, -1, 0, -1, -2 });
				return GeoRasterFactory.createGeoRaster(imagePlus, geoRaster
						.getMetadata());
			case SOUTHEAST:
				imagePlus.getProcessor().convolve3x3(
						new int[] { -2, -1, 0, -1, 1, 1, 0, 1, 2 });
				return GeoRasterFactory.createGeoRaster(imagePlus, geoRaster
						.getMetadata());
			case NORTHEAST:
				imagePlus.getProcessor().convolve3x3(
						new int[] { 0, 1, 2, -1, 1, 1, -2, -1, 0 });
				return GeoRasterFactory.createGeoRaster(imagePlus, geoRaster
						.getMetadata());
			case SOUTHWEST:
				imagePlus.getProcessor().convolve3x3(
						new int[] { 0, -1, -2, 1, 1, -1, 2, 1, 0 });
				return GeoRasterFactory.createGeoRaster(imagePlus, geoRaster
						.getMetadata());
			default:
				throw new OperationException("Unknown orientation: "
						+ orientation);
			}
		} catch (IOException e) {
			throw new OperationException("Cannot smooth the geoRaster", e);
		}
	}
}