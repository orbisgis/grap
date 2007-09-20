package org.grap.model;

import java.io.Serializable;

import com.vividsolutions.jts.geom.Envelope;

public class RasterMetadata implements Serializable {
	/**
	 * Upper left corner coordinate of first dimension. Be carefull It can be
	 * alos upper center left
	 */
	private double xllcorner = 0;

	/**
	 * Upper left corner coordinate of second dimension. Be carefull It can be
	 * alos upper center left
	 */
	private double yllcorner = 0;

	/** Resolution for the pixel. */
	private float pixelSize_X = 0;

	private float pixelSize_Y;

	private float noDataValue = 0;

	private int ncols = 0;

	private int nrows = 0;

	private Envelope envelope;

	/** Rotation on the first dimension. */

	private double rotationX = 0.0;

	/** Rotation on the second dimension. */
	private double rotationY = 0.0;

	public RasterMetadata() {
	}

	public RasterMetadata(final float x, final float y,
			final float pixelSize_X, final float pixelSize_Y, final int nodata) {
		this.xllcorner = x;
		this.yllcorner = y;
		this.pixelSize_X = pixelSize_X;
		this.pixelSize_Y = pixelSize_Y;
		this.noDataValue = nodata;
	}

	public double getXOrigin() {
		return xllcorner;
	}

	public void setXOrigin(final double xllcorner2) {
		xllcorner = xllcorner2;
	}

	public double getYOrigin() {
		return yllcorner;
	}

	public void setYOrigin(final double yllcorner2) {
		yllcorner = yllcorner2;
	}

	public float getPixelSize_X() {
		return pixelSize_X;
	}

	public void setPixelSize_X(final float pixelSize_X) {
		this.pixelSize_X = pixelSize_X;
	}

	public float getPixelSize_Y() {
		return pixelSize_Y;
	}

	public void setPixelSize_Y(final float pixelSize_Y) {
		this.pixelSize_Y = pixelSize_Y;
	}

	public String toString() {
		return new String("RasterMetadata with corners (" + xllcorner + ","
				+ yllcorner + ") and envelope ( " + envelope + ")");
	}

	public void setNoData(final float noDataValue) {
		this.noDataValue = noDataValue;
	}

	public float getNoDataValue() {
		return noDataValue;
	}

	public void setNCols(final int ncols) {
		this.ncols = ncols;
	}

	public int getNCols() {
		return ncols;
	}

	public void setNRows(final int nrows) {
		this.nrows = nrows;
	}

	public int getNRows() {
		return nrows;
	}

	public void setEnveloppe(final double minx, final double maxx,
			final double miny, final double maxy) {
		envelope = new Envelope(minx, maxx, miny, maxy);
	}

	public Envelope getEnvelope() {
		if (envelope == null) {
			envelope = computeEnvelope();
		}
		return envelope;
	}

	public Envelope computeEnvelope() {
		double xm, xM, ym, yM;

		xm = xllcorner;
		xM = xllcorner + (ncols * pixelSize_X);

		ym = yllcorner - (nrows * pixelSize_Y);
		yM = yllcorner;

		envelope = new Envelope(xm, xM, ym, yM);
		return envelope;
	}

	public double getRotation_X() {
		return rotationX;
	}

	public void setXRotation(final double rotationX) {
		this.rotationX = rotationX;
	}

	public double getRotation_Y() {
		return rotationY;
	}

	public void setYRotation(final double rotationY) {
		this.rotationY = rotationY;
	}

	public double getXllcorner() {
		return xllcorner;
	}

	public double getYllcorner() {
		return yllcorner;
	}
}