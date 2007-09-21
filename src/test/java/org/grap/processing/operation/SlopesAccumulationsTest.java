package org.grap.processing.operation;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;

public class SlopesAccumulationsTest {
	public static void main(String[] args) {
		final String src = "../../datas2tests/grid/sample.asc";
		// final String src = "../../datas2tests/grid/mntzee_500.asc";
		// final String src = "../../datas2tests/grid/saipan-5.asc";

		// load the DEM
		final GeoRaster grDEM = new GeoRaster(src);
		grDEM.open();

		// compute the slopes directions
		final Operation slopesDirections = new SlopesDirections();
		final GeoRaster grSlopesDirections = grDEM
				.doOperation(slopesDirections);

		// compute the slopes accumulations
		final Operation slopesAccumulations = new SlopesAccumulations();
		final GeoRaster grSlopesAccumulations = grSlopesDirections
				.doOperation(slopesAccumulations);

		grSlopesAccumulations.setLUT("fire");
		grSlopesAccumulations.show();
	}
}