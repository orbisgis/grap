package org.grap.processing.operation.manual;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.operation.AllWatersheds;
import org.grap.processing.operation.SlopesDirections;

public class AllWatershedsTest {
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		final String fileName = "../../datas2tests/grid/sample.asc";
		// final String fileName = "../../datas2tests/grid/mntzee_500.asc";
		// final String fileName = "../../datas2tests/grid/saipan-5.asc";

		// load the DEM
		final GeoRaster grDEM = GeoRasterFactory.read(fileName);

		// compute the slopes directions
		final Operation slopesDirections = new SlopesDirections();
		final GeoRaster grSlopesDirections = grDEM
				.doOperation(slopesDirections);

		// grSlopesDirections.setLUT("fire");
		// grSlopesDirections.show();
		// try {
		// grSlopesDirections.save("/tmp/dir.tif");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// compute all the watersheds
		final Operation allWatersheds = new AllWatersheds();
		final GeoRaster grAllWatersheds = grSlopesDirections
				.doOperation(allWatersheds);

		grAllWatersheds.setLUT("fire");
		grAllWatersheds.show();
	}
}