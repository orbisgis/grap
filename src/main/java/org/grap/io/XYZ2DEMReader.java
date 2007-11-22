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

package org.grap.io;

import java.io.IOException;
import java.net.URL;

import org.grap.model.GeoProcessorType;
import org.grap.model.GrapImagePlus;
import org.grap.model.RasterMetadata;

public class XYZ2DEMReader implements FileReader {
	private XYZ2DEM_Importer xyzImporter;

	// constructors
	public XYZ2DEMReader(final String fileName) {
		this(fileName, GeoProcessorType.FLOAT);
	}

	public XYZ2DEMReader(final String fileName,
			final GeoProcessorType geoProcessorType) {
		// this.geoProcessorType = geoProcessorType;
		xyzImporter = new XYZ2DEM_Importer(fileName);
	}

	public XYZ2DEMReader(final URL src) {
		this(src.getFile());
	}

	public GrapImagePlus readGrapImagePlus() throws IOException,
			GeoreferencingException {
		return new GrapImagePlus("", xyzImporter.ip);
	}

	public RasterMetadata readRasterMetadata() throws IOException,
			GeoreferencingException {
		return xyzImporter.rastermetadata;
	}
}