package org.grap.archive;

import ij.ImagePlus;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

/*
 * -Xmx3072m -XX:+AggressiveHeap
 */

public class GetGeoRasterInformation {
	private final static String PREF = "../../datas2tests/";
	private final static Object[] LIST = new Object[] {//
	// IndexCM + GRAY8
			PREF + "geotif/440606.tif", //
			PREF + "geotif/440808.tif", //
			PREF + "orbisgis_tuto/raster/f018_024.tif", //
			PREF + "orbisgis_tuto/raster/f018_025.tif", //
			// IndexCM + GRAY32
			PREF + "geotif/out.tif", //
			PREF + "geotif/3x3_origin.tif", //
			PREF + "MNT_Nantes.tif",//
			// DirectCM + COLOR_RGB
			PREF + "geotif/LeHavre.tif", //
			// "/home/leduc/data/Nantes/Nantes_est.tif", //

			// IndexCM + GRAY*
			new byte[0], //
			new short[0], //
			new float[0], //
	};

	private static String fromGeoRasterTypeToImagePlusTypeName(
			final int typeCode) {
		switch (typeCode) {
		case ImagePlus.GRAY8:
			return "GRAY8";
		case ImagePlus.GRAY16:
			return "GRAY16";
		case ImagePlus.GRAY32:
			return "GRAY32";
		case ImagePlus.COLOR_256:
			return "COLOR_256";
		case ImagePlus.COLOR_RGB:
			return "COLOR_RGB";
		}
		return "--- UNKNOWN ---";
	}

	public GetGeoRasterInformation(final Object object)
			throws FileNotFoundException, IOException {
		GeoRaster gr = null;

		if (object instanceof String) {
			gr = GeoRasterFactory.createGeoRaster((String) object);
		} else {
			final String className = object.getClass().getSimpleName();
			if (className.equals("byte[]")) {
				gr = GeoRasterFactory.createGeoRaster((byte[]) object,
						new RasterMetadata(0.5, 0.5, 1, -1, 0, 0));
			} else if (className.equals("short[]")) {
				gr = GeoRasterFactory.createGeoRaster((short[]) object,
						new RasterMetadata(0.5, 0.5, 1, -1, 0, 0));
			} else if (className.equals("float[]")) {
				gr = GeoRasterFactory.createGeoRaster((float[]) object,
						new RasterMetadata(0.5, 0.5, 1, -1, 0, 0));
			}
		}

	}

	public static void main(String[] args) throws Exception {
		for (Object img : LIST) {
			new GetGeoRasterInformation(img);
		}
	}
}