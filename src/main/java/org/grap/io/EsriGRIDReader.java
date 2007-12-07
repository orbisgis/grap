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

import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.grap.model.GeoProcessorType;
import org.grap.model.GrapImagePlus;
import org.grap.model.RasterMetadata;

/**
 *
 * @author Thomas and Erwan
 *
 * This class is written to directly access the ESRI ascii grid format.
 *
 * The ASCII grid data file format comprises a few lines of header data followed
 * by lists of cell values. The header data includes the following keywords and
 * values:
 *
 * ncols - number of columns in the data set. nrows - number of rows in the data
 * set. xllcenter or xllcorner - x-coordinate of the center or lower-left corner
 * of the uper-left cell. yllcenter or yllcorner - y-coordinate of the center or
 * lower-left corner of the lower-left cell. cellsize - cell size for the data
 * set. nodata_value - value in the file assigned to cells whose value is
 * unknown. This keyword and value is optional. The nodata_value defaults to
 * -9999.
 *
 *
 * For example
 *
 * ncols 480 nrows 450 xllcorner 378923 yllcorner 4072345 cellsize 30
 * nodata_value -32768 43 3 45 7 3 56 2 5 23 65 34 6 32 etc 35 45 65 34 2 6 78 4
 * 38 44 89 3 2 7 etc etc
 *
 */
public class EsriGRIDReader implements FileReader {
	private InputStream in;

	private float noDataValue;

	private RasterMetadata rasterMetadata;

	private GeoProcessorType geoProcessorType = GeoProcessorType.FLOAT;

	private String fileName;

	// constructors
	public EsriGRIDReader(final String fileName) {
		this(fileName, GeoProcessorType.FLOAT);
	}

	public EsriGRIDReader(final String fileName,
			final GeoProcessorType geoProcessorType) {
		this.fileName = fileName;
		this.geoProcessorType = geoProcessorType;
	}

	public EsriGRIDReader(final URL src) {
		this(src.getFile());
	}

	// private methods
	private final char readWhiteSpaces() throws IOException {
		int c = in.read();
		// ((' ' == c) || ('\t' == c) || ('\n' == c) || ('\r' == c));
		while ((32 == c) || (9 == c) || (10 == c) || (13 == c)) {
			c = in.read();
		}
		return (char) c;
	}

	private final String readString() throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append(readWhiteSpaces());
		int c = in.read();
		while ((32 != c) && (9 != c) && (10 != c) && (13 != c) && (-1 != c)) {
			sb.append((char) c);
			c = in.read();
		}
		return sb.toString();
	}

	private final String readNumber(final char initial) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append(initial);
		int c = in.read();
		while (((c > 47) && (c < 58)) || (45 == c) || (46 == c)) {
			sb.append((char) c);
			c = in.read();
		}
		return sb.toString();
	}

	private final int readInteger() throws NumberFormatException, IOException {
		return Integer.parseInt(readNumber(readWhiteSpaces()));
	}

	private final float readFloat() throws NumberFormatException, IOException {
		return Float.parseFloat(readNumber(readWhiteSpaces()));
	}

	private final double readDouble() throws NumberFormatException, IOException {
		return Double.parseDouble(readNumber(readWhiteSpaces()));
	}

	private final float[] readFloatBody(final int n) throws IOException {
		final float[] fValues = new float[n];
		for (int i = 0; i < n; i++) {
			final float tmp = readFloat();
			fValues[i] = (noDataValue == tmp) ? Float.NaN : tmp;
		}
		return fValues;
	}

	private final short[] readShortBody(final int n) throws IOException {
		final short[] sValues = new short[n];
		for (int i = 0; i < n; i++) {
			sValues[i] = (short) readInteger();
		}
		return sValues;
	}

	private final byte[] readByteBody(final int n) throws IOException {
		final byte[] bValues = new byte[n];
		for (int i = 0; i < n; i++) {
			bValues[i] = (byte) readInteger();
		}
		return bValues;
	}

	private final void open() throws IOException {
		InputStream src = new BufferedInputStream(new FileInputStream(fileName));

		if (fileName.toLowerCase().endsWith(".gz")) {
			src = new GZIPInputStream(src);
		} else if (fileName.toLowerCase().endsWith(".zip")) {
			src = new ZipInputStream(src); // needs to be tested
		}

		in = new BufferedInputStream(src);
	}

	private final void close() throws IOException {
		if (null != in) {
			in.close();
		}
	}

	private void readString(final String stringToCompareWith)
			throws GeoreferencingException, IOException {
		if (!readString().equalsIgnoreCase(stringToCompareWith)) {
			throw new GeoreferencingException("Invalid EsriGRID format ("
					+ stringToCompareWith + ")!");
		}
	}

	private final RasterMetadata readHeaderPart()
			throws GeoreferencingException, IOException {
		readString("ncols");
		final int nCols = readInteger();
		readString("nrows");
		final int nRows = readInteger();
		readString("xllcorner");
		final double xOrigin = readDouble();
		readString("yllcorner");
		final double yOrigin = readDouble();
		readString("cellsize");
		final float cellsize = readFloat();
		readString("NODATA_value");
		noDataValue = readFloat();

		final double upperLeftX = xOrigin;
		final double upperLeftY = yOrigin + nRows * cellsize;

		// the -cellsize in case of pixelSize_Y parameter is due to the world
		// file specifications
		rasterMetadata = new RasterMetadata(upperLeftX + cellsize / 2,
				upperLeftY - cellsize / 2, cellsize, -cellsize, nCols, nRows);
		rasterMetadata.setNoData(noDataValue);
		return rasterMetadata;
	}

	// public methods
	public final RasterMetadata readRasterMetadata()
			throws GeoreferencingException, IOException {
		open();
		rasterMetadata = readHeaderPart();
		close();
		return rasterMetadata;
	}

	public GrapImagePlus readGrapImagePlus() throws GeoreferencingException,
			IOException {
		open();
		readHeaderPart();
		ImageProcessor imageProcessor = null;
		switch (geoProcessorType) {
		case BYTE:
			imageProcessor = new ByteProcessor(rasterMetadata.getNCols(),
					rasterMetadata.getNRows(), readByteBody(rasterMetadata
							.getNCols()
							* rasterMetadata.getNRows()), null);
			break;
		case SHORT:
			imageProcessor = new ShortProcessor(rasterMetadata.getNCols(),
					rasterMetadata.getNRows(), readShortBody(rasterMetadata
							.getNCols()
							* rasterMetadata.getNRows()), null);
			break;
		case FLOAT:
			imageProcessor = new FloatProcessor(rasterMetadata.getNCols(),
					rasterMetadata.getNRows(), readFloatBody(rasterMetadata
							.getNCols()
							* rasterMetadata.getNRows()), null);
			break;
		default:
			throw new RuntimeException("Unknown geoProcessorType : "
					+ geoProcessorType);
		}
		close();
		return new GrapImagePlus("", imageProcessor);
	}
}