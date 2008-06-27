package org.grap.processing.operation.manual.hydrology;

import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.operation.hydrology.D8OpAccumulation;
import org.grap.processing.operation.hydrology.D8OpAllOutlets;
import org.grap.processing.operation.hydrology.D8OpAllWatersheds;
import org.grap.processing.operation.hydrology.D8OpDirection;
import org.grap.processing.operation.hydrology.D8OpWatershedsWithThreshold;

public class WatershedWithThresholdTest {
	public static void main(String[] args) throws Exception {
		final String src = "../../datas2tests/grid/sample.asc";
		// final String src = "../../datas2tests/grid/mntzee_500.asc";
		// final String src = "../../datas2tests/grid/saipan-5.asc";

		// load the DEM
		final GeoRaster grDEM = GeoRasterFactory.createGeoRaster(src);
		grDEM.open();

		// compute the slopes directions
		final Operation slopesDirections = new D8OpDirection();
		final GeoRaster grSlopesDirections = grDEM
				.doOperation(slopesDirections);
		grSlopesDirections.save("../../datas2tests/tmp/1.tif");

		// compute the slopes accumulations
		final Operation slopesAccumulations = new D8OpAccumulation();
		final GeoRaster grSlopesAccumulations = grSlopesDirections
				.doOperation(slopesAccumulations);
		grSlopesAccumulations.save("../../datas2tests/tmp/11.tif");

		// find all the outlets
		final Operation allOutlets = new D8OpAllOutlets();
		final GeoRaster grAllOutlets = grSlopesDirections
				.doOperation(allOutlets);
		grAllOutlets.save("../../datas2tests/tmp/111.tif");

		// compute all the watersheds
		final Operation allWatersheds = new D8OpAllWatersheds();
		final GeoRaster grAllWatersheds = grSlopesDirections
				.doOperation(allWatersheds);
		grAllWatersheds.save("../../datas2tests/tmp/1111.tif");

		// extract some "big" watersheds
		final int threshold = 100;
		final Operation watershedsWithThreshold = new D8OpWatershedsWithThreshold(
				grAllWatersheds, grAllOutlets, threshold);
		final GeoRaster grWatershedsWithThreshold = grSlopesAccumulations
				.doOperation(watershedsWithThreshold);

		grWatershedsWithThreshold.getImagePlus().getProcessor()
				.setColorModel(LutGenerator.colorModel("fire"));
		grWatershedsWithThreshold.show();
		grWatershedsWithThreshold.save("../../datas2tests/tmp/2.tif");
	}
}