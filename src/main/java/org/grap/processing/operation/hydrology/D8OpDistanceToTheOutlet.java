package org.grap.processing.operation.hydrology;

import java.io.IOException;
import java.util.Stack;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

public class D8OpDistanceToTheOutlet extends D8OpAbstract implements Operation {
	public final static float notProcessedYet = 0;

	private HydrologyUtilities hydrologyUtilities;
	private float[] d8Distances;
	private int ncols;
	private int nrows;

	@Override
	public GeoRaster evaluateResult(GeoRaster direction, IProgressMonitor pm)
			throws OperationException {
		try {
			hydrologyUtilities = new HydrologyUtilities(direction);

			final RasterMetadata rasterMetadata = direction.getMetadata();
			nrows = rasterMetadata.getNRows();
			ncols = rasterMetadata.getNCols();
			calculateDistances(pm);
			final GeoRaster grDistancesToTheOutlet = GeoRasterFactory
					.createGeoRaster(d8Distances, rasterMetadata);
			grDistancesToTheOutlet.setNodataValue(hydrologyUtilities.ndv);
			return grDistancesToTheOutlet;
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private void calculateDistances(IProgressMonitor pm) throws IOException {
		// distances' array initialization
		d8Distances = new float[nrows * ncols];

		for (int y = 0, i = 0; y < nrows; y++) {

			if (y / 100 == y / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * y / nrows));
				}
			}

			for (int x = 0; x < ncols; x++, i++) {
				if (hydrologyUtilities.isABorder(x, y)
						|| Float.isNaN(hydrologyUtilities.getPixelValue(x, y))) {
					d8Distances[i] = hydrologyUtilities.ndv;
				} else if (notProcessedYet == d8Distances[i]) {
					// current cell value has not been yet modified...
					final Stack<HydroCell> path = new Stack<HydroCell>();
					HydroCell top = hydrologyUtilities.shortHydrologicalPath(i,
							path, d8Distances, 1);

					float accumulDist = (null == top) ? 0 : top.dist;
					while (!path.empty()) {
						HydroCell cell = path.pop();
						accumulDist += cell.dist;
						d8Distances[cell.index] = accumulDist;
					}
				}
			}
		}
	}
}