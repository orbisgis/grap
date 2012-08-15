/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.grap.archive;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.TextReader;
import ij.process.ImageProcessor;

public class ManualNodataValue {
	public static void main(String[] args) {
		// Opener opener = new Opener();
		// ImagePlus imp = opener.openImage(src );
		final String src = "../../datas2tests/grid/ijsample.asc";
		final TextReader textReader = new TextReader();
		final ImageProcessor ip = textReader.open(src);
		System.out.println(ip.getMin());
		System.out.println(ip.getMax());

		// Cette option permet d'ajuster les valeurs affichées.
		// ip.setMinAndMax(0, 500);

		ip.setThreshold(0.0d, 500.0d, ImageProcessor.NO_LUT_UPDATE);

		// ip.setBackgroundValue(550d);
		final ImagePlus imp = new ImagePlus("", ip);
		WindowManager.setTempCurrentImage(imp);
		IJ.run("NaN Background");

		imp.show();
		System.out.println(imp.getProcessor().getf(0, 0));
		System.out.println(ip.getPixelValue(10, 10));
	}
}