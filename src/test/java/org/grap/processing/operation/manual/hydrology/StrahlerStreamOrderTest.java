package org.grap.processing.operation.manual.hydrology;

import org.grap.lut.LutGenerator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.operation.hydrology.D8OpAccumulation;
import org.grap.processing.operation.hydrology.D8OpDirection;
import org.grap.processing.operation.hydrology.D8OpStrahlerStreamOrder;

public class StrahlerStreamOrderTest {
	public static void main(String[] args) throws Exception {
		final String src = "../../datas2tests/grid/sample.asc";
		// final String src = "../../datas2tests/grid/mntzee_500.asc";
		// final String src = "../../datas2tests/grid/saipan-5.asc";

		// load the DEM
		final GeoRaster grDEM = GeoRasterFactory.createGeoRaster(src);
		grDEM.open();

		grDEM.show();

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

		// compute the Strahler stream orders
		final int riverThreshold = 100;
		final Operation strahlerStreamOrder = new D8OpStrahlerStreamOrder(
				grSlopesAccumulations, riverThreshold);
		final GeoRaster grStrahlerStreamOrder = grSlopesDirections
				.doOperation(strahlerStreamOrder);

		grStrahlerStreamOrder.getImagePlus().getProcessor().setColorModel(
				LutGenerator.colorModel("fire"));
		grStrahlerStreamOrder.show();
		grStrahlerStreamOrder.save("../../datas2tests/tmp/2.tif");
	}
}