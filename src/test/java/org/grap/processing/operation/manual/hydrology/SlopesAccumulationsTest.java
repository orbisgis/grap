package org.grap.processing.operation.manual.hydrology;

import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.operation.hydrology.D8OpAccumulation;
import org.grap.processing.operation.hydrology.D8OpDirection;

public class SlopesAccumulationsTest {
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
		grSlopesDirections.save("/tmp/nousdir.asc");

		// compute the slopes accumulations
		final Operation slopesAccumulations = new D8OpAccumulation();
		final GeoRaster grSlopesAccumulations = grSlopesDirections
				.doOperation(slopesAccumulations);

		grSlopesAccumulations.getImagePlus().getProcessor().setColorModel(
				LutGenerator.colorModel("fire"));
		grSlopesAccumulations.show();
		grSlopesAccumulations.save("../../datas2tests/tmp/2.tif");
	}
}