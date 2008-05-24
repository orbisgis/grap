/*
 * The GRAP library (GeoRAster Processing) is a middleware dedicated
 * to the processing of various kinds of geographic raster data. It
 * provides a complete and robust API to manipulate ASCII Grid or
 * tiff, png, bmp, jpg (with the corresponding world file) geographic
 * images. GRAP is produced  by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GRAP.
 *
 * GRAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAP. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.grap.processing.operation.topographicIndices;

import ij.process.ImageProcessor;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;


/**
 * LS factor is a slope length index used by the Universal Soil Loss Equation (USLE).
 * 
 *  A procedure  for estimating the LS-factor using contributing area is provided by
 *  Moore et al. (1993).
 *  
 *  LS = (n+1) [A s /22.13].n [sin B / 0.0896]m
 *  
 *  where  n = contant (0.4),
 *  B = local slope gradient (degrees),
 *  m = constant (1.3)
 *  A s =  unit contributind area
 *  
 * @author bocher
 *
 */

public class LSFactor implements Operation {


	private static final double ALMOST_ZERO = 0.0011;

	private int ncols;
	private int nrows;
	private ImageProcessor m_Slope;
	private ImageProcessor slope;
	private GeoRaster accFlow;
	private ImageProcessor m_LSFactor;
	private ImageProcessor m_accFlow;
	private float cellSize;

	private final static double FACTOR = 180 / Math.PI;

	
	public LSFactor(final GeoRaster accFlow){
		this.accFlow=accFlow;
	}

	public GeoRaster execute(final GeoRaster geoRaster, IProgressMonitor pm)
			throws OperationException {

		return processAlgorithm(geoRaster);
	}



	public GeoRaster processAlgorithm(final GeoRaster geoRaster){



		try {
			m_Slope = geoRaster.getImagePlus().getProcessor();
			
			//Convert the slope from radians to degrees.
			m_Slope.multiply(FACTOR);
			m_accFlow = accFlow.getImagePlus().getProcessor();
			
			nrows = geoRaster.getMetadata().getNRows() ;
			ncols = geoRaster.getMetadata().getNCols();
			cellSize = geoRaster.getMetadata().getPixelSize_X();

			m_accFlow.multiply(cellSize * cellSize);
			
			m_LSFactor =   m_Slope.duplicate();

			m_LSFactor.multiply(0);


			int x,y;

			for(y = 0; y<ncols; y++){
				for(x = 0; x < nrows; x++){

					float dSlope = m_Slope.getPixelValue(x, y);
					float dAccFlow = m_accFlow.getPixelValue(x, y);

					if (((Float.isNaN(dSlope))||(Float.isNaN(dAccFlow)))){
						m_LSFactor.putPixelValue(x, y, Float.NaN);

					}
					else {
						dAccFlow /= cellSize;
						dSlope = (float) Math.max(Math.tan(dSlope),  ALMOST_ZERO);
						m_LSFactor.putPixelValue(x, y,(0.4 + 1) * Math.pow(dAccFlow / 22.13, 0.4)
								* Math.pow(Math.sin(dSlope) / 0.0896, 1.3));
					}
				}
			}


		} catch (IOException e) {
			e.printStackTrace();
		}





		return GeoRasterFactory.createGeoRaster(m_LSFactor, geoRaster.getMetadata());

	}




}