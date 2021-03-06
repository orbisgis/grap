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
package org.grap.processing.operation.hydrology.archive;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.operation.hydrology.D8OpAccumulation;
import org.grap.processing.operation.hydrology.D8OpAllOutlets;
import org.grap.processing.operation.hydrology.D8OpAllWatersheds;
import org.grap.processing.operation.hydrology.D8OpDirection;
import org.grap.processing.operation.hydrology.D8OpWatershedsWithThreshold;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore("These tests shall disappear with the hydrology code in grap: they will be implemented on top of JAI.")
public class WatershedWithThresholdTest extends GrapTest {

        private GeoRaster geoRasterSrc;

        @Before
        public void setUp() throws Exception {
                geoRasterSrc = sampleDEM;
        }

        @Test
        public void testWatershedFromOutletIndex() throws Exception {
                // load the DEM
                geoRasterSrc.open();

                // compute the slopes directions
                final Operation slopesDirections = new D8OpDirection();
                final GeoRaster grSlopesDirections = geoRasterSrc.doOperation(slopesDirections);

                // compute the slopes accumulations
                final Operation slopesAccumulations = new D8OpAccumulation();
                final GeoRaster grSlopesAccumulations = grSlopesDirections.doOperation(slopesAccumulations);

                // find all the outlets
                final Operation allOutlets = new D8OpAllOutlets();
                final GeoRaster grAllOutlets = grSlopesDirections.doOperation(allOutlets);

                // compute all the watersheds
                final Operation allWatersheds = new D8OpAllWatersheds();
                final GeoRaster grAllWatersheds = grSlopesDirections.doOperation(allWatersheds);

                // extract some "big" watersheds
                int threshold = 49;
                Operation watershedsWithThreshold = new D8OpWatershedsWithThreshold(
                        grAllWatersheds, grAllOutlets, threshold);
                GeoRaster grWatershedsWithThreshold = grSlopesAccumulations.doOperation(watershedsWithThreshold);

                // compare the computed watersheds with previous ones
                printGeoRasterAndArray(grWatershedsWithThreshold,
                        otherAllWatershedsForDEM);
                compareGeoRasterAndArray(grWatershedsWithThreshold,
                        otherAllWatershedsForDEM);

                // extract some "big" watersheds
                threshold = 50;
                watershedsWithThreshold = new D8OpWatershedsWithThreshold(
                        grAllWatersheds, grAllOutlets, threshold);
                grWatershedsWithThreshold = grSlopesAccumulations.doOperation(watershedsWithThreshold);

                for (int r = 0; r < grWatershedsWithThreshold.getHeight(); r++) {
                        for (int c = 0; c < grWatershedsWithThreshold.getWidth(); c++) {
                                assertTrue(Float.isNaN(grWatershedsWithThreshold.getImagePlus().getProcessor().getPixelValue(c, r)));
                        }
                }
        }
}