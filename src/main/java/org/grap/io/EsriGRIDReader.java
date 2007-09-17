package org.grap.io;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

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
public class EsriGRIDReader {
	private InputStream in = null;

	public float[] fValues = null;

	private short[] iValues = null;

	public int ncols = 0;

	public int nrows = 0;

	private double xllcorner = Double.MAX_VALUE;

	private double yllcorner = Double.MAX_VALUE;

	private float cellsize = Integer.MIN_VALUE;

	private float noDataValue = Integer.MIN_VALUE;

	String fileName = null;

	RasterMetadata rasterMetadata = new RasterMetadata(0.0F, 0.0F, 0.0F, 0.0F,
			0);

	/**
	 * 
	 * @param fileName
	 * @throws IOException
	 */

	public EsriGRIDReader(final String fileName) throws IOException {

		this(new FileInputStream(fileName), fileName);
		this.fileName = fileName;

	}

	public EsriGRIDReader(final URL src) throws IOException {
		this(src.openStream(), src.getFile());
	}

	public EsriGRIDReader(InputStream src, final String srcName)
			throws IOException {
		if (srcName.toLowerCase().endsWith(".gz"))
			src = new GZIPInputStream(src);
		else if (srcName.toLowerCase().endsWith(".zip"))
			src = new ZipInputStream(src); // pose pb
		in = new BufferedInputStream(src);
	}

	// private methods
	private final char readWhiteSpaces() throws IOException {
		int c = in.read();
		// ((' ' == c) || ('\t' == c) || ('\n' == c) || ('\r' == c));
		while ((32 == c) || (9 == c) || (10 == c) || (13 == c))
			c = in.read();
		return (char) c;
	}

	private final String readString() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(readWhiteSpaces());
		int c = in.read();
		while ((32 != c) && (9 != c) && (10 != c) && (13 != c) && (-1 != c)) {
			sb.append((char) c);
			c = in.read();
		}
		return sb.toString();
	}

	private final String readNumber(final char initial) throws IOException {
		StringBuilder sb = new StringBuilder();
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

	private final void readIntegerBody(final int n) throws IOException {
		iValues = new short[n];
		for (int i = 0; i < n; i++) {
			iValues[i] = (short) readInteger();
		}
	}

	// public methods
	public final RasterMetadata readHead() throws IOException {
		readString();
		ncols = readInteger();
		rasterMetadata.setNCols(ncols);
		readString();
		nrows = readInteger();
		rasterMetadata.setNRows(nrows);
		readString();
		xllcorner = readDouble();
		rasterMetadata.setXOrigin(xllcorner);
		readString();
		yllcorner = readDouble();
		rasterMetadata.setYOrigin(yllcorner);
		readString();
		cellsize = readFloat();
		rasterMetadata.setPixelSize_X(cellsize);
		rasterMetadata.setPixelSize_Y(cellsize);
		readString();
		noDataValue = readFloat();
		rasterMetadata.setNoData(noDataValue);
		rasterMetadata.computeEnvelope();

		return rasterMetadata;
	}

	public void fRead() throws IOException {
		readHead();
		readFloatBody(ncols * nrows);
		close();
	}

	public void iRead() throws IOException {
		readHead();
		readIntegerBody(ncols * nrows);
		close();
	}

	public void close() throws IOException {
		in.close();
	}

	public void printHeaderValues() throws IllegalArgumentException,
			IllegalAccessException {
		for (Field x : getClass().getDeclaredFields()) {
			if (x.getType().isPrimitive())
				System.err.printf("%12s : %s\n", x.getName(), x.get(this));
		}
	}

	public void printMinMax() {
		if (null != fValues) {
			float min = Float.MAX_VALUE;
			float max = Float.MIN_VALUE;
			for (float item : fValues) {
				if (item > max)
					max = item;
				if (item < min)
					min = item;
			}
			System.out.printf("min = %g\nmax = %g\n", min, max);
		} else if (null != iValues) {
			int min = Integer.MAX_VALUE;
			int max = Integer.MIN_VALUE;
			for (int item : iValues) {
				if (item > max)
					max = item;
				else if (item < min)
					min = item;
			}
			System.out.printf("min = %d\nmax = %d\n", min, max);
		} else {
			System.out.printf("min = ?\nmax = ?\n");
		}
	}

	public ImageProcessor getFloatProcessor() throws IOException {
		fRead();

		ImageProcessor ip = new FloatProcessor(ncols, nrows, fValues, null);

		return ip;
	}

	public ImagePlus getFloatImagePlus() throws IOException {

		return new ImagePlus("", getFloatProcessor());
	}

	public ImageProcessor getIntProcessor() throws IOException {
		iRead();

		ImageProcessor ip = new ShortProcessor(ncols, nrows, iValues, null);

		return ip;
	}

	public ImagePlus getIntImagePlus() throws IOException {

		return new ImagePlus("", getIntProcessor());
	}

	public RasterMetadata getRasterMetadata() {

		return rasterMetadata;
	}

	public static void main(String[] args) throws IOException,
			IllegalArgumentException, IllegalAccessException {

		String src = "C://temp/grid//grille2.txt";

		long start = System.currentTimeMillis();
		EsriGRIDReader ar = new EsriGRIDReader(src);
		ar.fRead();

		ImageProcessor imp = new FloatProcessor(ar.ncols, ar.nrows, ar.fValues,
				null);

		// Get the pixel value
		System.out.println(imp.getPixelValue(0, 3));

		ImagePlus imagePlus = new ImagePlus("Ascii grid", imp);

		Calibration calibration = new Calibration();

		// pixel distance in the world
		double known = ar.cellsize;

		// In general equal 1
		double measured = 1;

		double resolution = known / measured;

		calibration.pixelHeight = resolution;
		calibration.pixelWidth = resolution;

		// Unit� de mesure
		calibration.setUnit("m");

		imagePlus.setCalibration(calibration);

		// imagePlus.show();

		FileSaver save = new FileSaver(imagePlus);

		save.saveAsText("../datas2tests/temp/outsample2.asc");

		System.out.println(System.currentTimeMillis() - start);

	}

}