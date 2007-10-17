package org.grap.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.Serializable;

import com.vividsolutions.jts.geom.Envelope;

public class RasterMetadata implements Serializable {
	private double xulcorner = 0;

	private double yulcorner = 0;

	/** Resolution for the pixel. */

	private float pixelSize_X = 1;

	private float pixelSize_Y = 1;

	private float noDataValue = 0;

	private int ncols = 0;

	private int nrows = 0;

	private Envelope envelope;

	/** Rotation on the first dimension. */

	private double rotationX = 0.0;

	/** Rotation on the second dimension. */
	private double rotationY = 0.0;

	private AffineTransform affineTransform;

	private AffineTransform inverseTransform;

	public RasterMetadata(final double upperLeftX, final double upperLeftY,
			final float pixelSize_X, final float pixelSize_Y, int ncols,
			int nrows) {
		this(upperLeftX, upperLeftY, pixelSize_X, pixelSize_Y, ncols, nrows, 0,
				0);
	}

	public RasterMetadata(final double upperLeftX, final double upperLeftY,
			final float pixelSize_X, final float pixelSize_Y, int ncols,
			int nrows, final double colRotation, final double rowRotation) {
		this.xulcorner = upperLeftX;
		this.yulcorner = upperLeftY;
		this.pixelSize_X = pixelSize_X;
		this.pixelSize_Y = pixelSize_Y;
		this.rotationX = rowRotation;
		this.rotationY = colRotation;
		this.nrows = nrows;
		this.ncols = ncols;
		calculateAffineTransform();
	}

	/**
	 * returns upper left corner X coordinate.
	 * 
	 * @return type double.
	 */

	public double getXOrigin() {
		return xulcorner;
	}

	/**
	 * returns upper left corner Y coordinate.
	 * 
	 * @return type double.
	 */

	public double getYOrigin() {
		return yulcorner;
	}

	/**
	 * returns pixel's width.
	 * 
	 * @return type int.
	 */

	public float getPixelSize_X() {
		return pixelSize_X;
	}

	/**
	 * returns pixel's high.
	 * 
	 * @return type int.
	 */

	public float getPixelSize_Y() {
		return pixelSize_Y;
	}

	public String toString() {
		return new String("RasterMetadata with corners (" + xulcorner + ","
				+ yulcorner + ") and envelope ( " + getEnvelope() + ")");
	}

	public void setNoData(final float noDataValue) {
		this.noDataValue = noDataValue;
	}

	public float getNoDataValue() {
		return noDataValue;
	}

	public int getNCols() {
		return ncols;
	}

	public int getNRows() {
		return nrows;
	}

	public Envelope getEnvelope() {
		if (envelope == null) {
			envelope = computeEnvelope();
		}
		return envelope;
	}

	private Envelope computeEnvelope() {
		double xm, xM, ym, yM;
		xm = xulcorner - pixelSize_X / 2;
		xM = xm + (ncols * pixelSize_X);

		yM = yulcorner - pixelSize_Y / 2;
		ym = yM + (nrows * pixelSize_Y);

		envelope = new Envelope(xm, xM, ym, yM);
		return envelope;
	}

	public double getRotation_X() {
		return rotationX;
	}

	public double getRotation_Y() {
		return rotationY;
	}

	public double getXllcorner() {
		return xulcorner;
	}

	public double getYllcorner() {
		return yulcorner;
	}

	private void calculateAffineTransform() {
		affineTransform = AffineTransform.getRotateInstance(rotationX,
				rotationY);
		affineTransform.translate(xulcorner, yulcorner);
		affineTransform.scale(pixelSize_X, pixelSize_Y);
	}

	public Point2D toPixel(final double x, final double y) {
		return getInverse().transform(new Point2D.Double(x, y), null);
	}

	private AffineTransform getInverse() {
		if (null == inverseTransform) {
			try {
				inverseTransform = affineTransform.createInverse();
			} catch (NoninvertibleTransformException e) {
				throw new RuntimeException(e);
			}

		}

		return inverseTransform;
	}

	public Point2D toWorld(final int x, final int y) {
		return affineTransform.transform(new Point2D.Double(x, y), null);
	}

	public RasterMetadata duplicate() {
		final RasterMetadata ret = new RasterMetadata(xulcorner, yulcorner,
				pixelSize_X, pixelSize_Y, ncols, nrows, rotationX, rotationY);

		ret.noDataValue = this.noDataValue;
		ret.envelope = new Envelope(getEnvelope());
		ret.calculateAffineTransform();

		return ret;
	}
}