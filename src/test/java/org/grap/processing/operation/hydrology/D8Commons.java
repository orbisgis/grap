package org.grap.processing.operation.hydrology;

import ij.ImagePlus;
import junit.framework.TestCase;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;

public class D8Commons extends TestCase {
	public static String rep = "../../datatestjunit/hydrology/";

	private static final double EPSILON = 1E-6;

	public static boolean equals(final GeoRaster gr1, final GeoRaster gr2)
			throws Exception {
		return equals(gr1, gr2, false);
	}

	public static boolean equals(final GeoRaster gr1, final GeoRaster gr2,
			final boolean flag) throws Exception {
		gr1.open();
		gr2.open();
		RasterMetadata rmd1 = gr1.getMetadata();
		RasterMetadata rmd2 = gr2.getMetadata();

		if ((rmd1.getNRows() == rmd2.getNRows())
				&& (rmd1.getNCols() == rmd2.getNCols())
				&& (rmd1.getPixelSize_X() == rmd2.getPixelSize_X())
				&& (rmd1.getPixelSize_Y() == rmd2.getPixelSize_Y())
				&& (rmd1.getXulcorner() == rmd2.getXulcorner())
				&& (rmd1.getYulcorner() == rmd2.getYulcorner())
				&& (rmd1.getRotation_X() == rmd2.getRotation_X())
				&& (rmd1.getRotation_Y() == rmd2.getRotation_Y())
		// && (rmd1.getNoDataValue() == rmd2.getNoDataValue())
		) {
			ImagePlus ip1 = gr1.getImagePlus();
			ImagePlus ip2 = gr2.getImagePlus();

			int cpt = 0;
			for (int x = 0; x < gr1.getWidth(); x++) {
				for (int y = 0; y < gr1.getHeight(); y++) {
					if (!floatingPointNumbersEquality(ip1.getProcessor()
							.getPixelValue(x, y), ip2.getProcessor()
							.getPixelValue(x, y))) {
						cpt++;
						if (flag) {
							System.out
									.printf(
											"[x = %d, y = %d] %g != %g (cpt = %d)\n",
											x, y, ip1.getProcessor()
													.getPixelValue(x, y), ip2
													.getProcessor()
													.getPixelValue(x, y), cpt);
						}
					}
				}
			}
			System.out.printf("%d inegalites sur %d * %d = %d\n", cpt, rmd1
					.getNRows(), rmd1.getNCols(), rmd1.getNRows()
					* rmd1.getNCols());
			return (cpt > 0) ? false : true;
		}
		return false;
	}

	public static boolean floatingPointNumbersEquality(final double a,
			final double b) {
		if (Double.isNaN(a)) {
			return Double.isNaN(b);
		} else {
			return Math.abs(a - b) < EPSILON;
		}
	}
}