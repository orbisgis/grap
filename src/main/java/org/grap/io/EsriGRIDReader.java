package org.grap.io;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.grap.model.GeoProcessorType;
import org.grap.model.GeoRaster;
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
	private InputStream src;

	private InputStream in;

	private float[] fValues;

	private short[] sValues;

	private byte[] bValues;;

	private float noDataValue = Float.MIN_VALUE;

	private RasterMetadata rasterMetadata;

	private GeoProcessorType geoProcessorType;

	private ImageProcessor imageProcessor;

	// constructors
	public EsriGRIDReader(final String fileName,
			final GeoProcessorType geoProcessorType)
			throws FileNotFoundException, IOException {
		this(new FileInputStream(fileName), fileName, geoProcessorType);
	}

	public EsriGRIDReader(final URL src, final GeoProcessorType geoProcessorType)
			throws IOException {
		this(src.openStream(), src.getFile(), geoProcessorType);
	}

	public EsriGRIDReader(final InputStream src, final String srcName,
			final GeoProcessorType geoProcessorType) throws IOException {
		if (srcName.toLowerCase().endsWith(".gz")) {
			this.src = new GZIPInputStream(src);
		} else if (srcName.toLowerCase().endsWith(".zip")) {
			this.src = new ZipInputStream(src); // pose pb
		} else {
			this.src = src;
		}
		this.geoProcessorType = geoProcessorType;
	}

	// private methods
	private void open() {
		if (null == in) {
			in = new BufferedInputStream(src);
		}
	}

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

	private final void readFloatBody(final int n) throws IOException {
		fValues = new float[n];
		for (int i = 0; i < n; i++) {
			final float tmp = readFloat();
			fValues[i] = (noDataValue == tmp) ? Float.NaN : tmp;
		}
	}

	private final void readShortBody(final int n) throws IOException {
		sValues = new short[n];
		for (int i = 0; i < n; i++) {
			sValues[i] = (short) readInteger();
		}
	}

	private final void readByteBody(final int n) throws IOException {
		sValues = new short[n];
		for (int i = 0; i < n; i++) {
			bValues[i] = (byte) readInteger();
		}
	}

	private void close() throws IOException {
		if (null != in) {
			in.close();
		}
	}

	// public methods
	public final RasterMetadata getRasterMetadata() throws IOException {
		if (null == rasterMetadata) {
			open();
			rasterMetadata = new RasterMetadata();
			readString();
			rasterMetadata.setNCols(readInteger());
			readString();
			rasterMetadata.setNRows(readInteger());
			readString();
			rasterMetadata.setXOrigin(readDouble());
			readString();
			rasterMetadata.setYOrigin(readDouble());
			readString();
			final float cellsize = readFloat();
			rasterMetadata.setPixelSize_X(cellsize);
			rasterMetadata.setPixelSize_Y(cellsize);
			readString();
			noDataValue = readFloat();
			rasterMetadata.setNoData(noDataValue);
			rasterMetadata.computeEnvelope();
		}
		return rasterMetadata;
	}

	public GeoRaster read() throws IOException {
		open();
		getRasterMetadata();

		switch (geoProcessorType) {
		case BYTE:
			if (null == bValues) {
				readByteBody(rasterMetadata.getNCols()
						* rasterMetadata.getNRows());
				imageProcessor = new ByteProcessor(rasterMetadata.getNCols(),
						rasterMetadata.getNRows(), bValues, null);
			}
			break;
		case SHORT:
			if (null == sValues) {
				readShortBody(rasterMetadata.getNCols()
						* rasterMetadata.getNRows());
				imageProcessor = new ShortProcessor(rasterMetadata.getNCols(),
						rasterMetadata.getNRows(), sValues, null);
			}
			break;
		case FLOAT:
			if (null == fValues) {
				readFloatBody(rasterMetadata.getNCols()
						* rasterMetadata.getNRows());
				imageProcessor = new FloatProcessor(rasterMetadata.getNCols(),
						rasterMetadata.getNRows(), fValues, null);
			}
			break;
		default:
			throw new RuntimeException("Unknown geoProcessorType : "
					+ geoProcessorType);
		}

		close();
		return new GeoRaster(new ImagePlus("", imageProcessor), rasterMetadata);
	}
}