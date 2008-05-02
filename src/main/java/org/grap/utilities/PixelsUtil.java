package org.grap.utilities;

import java.awt.geom.Point2D;
import java.io.IOException;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GrapImagePlus;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public class PixelsUtil {

	
    
	private GeoRaster geoRaster;
	private GrapImagePlus grapImagePlus;
	private double _2DX;
	private double _6DX;
	private float _DX_2;
	private double _4DX_2;

	/* neighbor's address*/                        
	private final static int m_iOffsetX []=        {  0,  1,  1,  1,  0, -1, -1, -1};
	private final static int m_iOffsetY []=        {  1,  1,  0, -1, -1, -1,  0,  1};
	
	

	public final static double DEG_45_IN_RAD = Math.PI / 180. * 45.;
    public final static double DEG_90_IN_RAD = Math.PI / 180. * 90.;
    public final static double DEG_180_IN_RAD = Math.PI ;
    public final static double DEG_270_IN_RAD = Math.PI / 180. * 270.;
    public final static double DEG_360_IN_RAD = Math.PI * 2.;
	
	public PixelsUtil(final GeoRaster geoRaster){
		this.geoRaster=geoRaster;
		 try {
			grapImagePlus = geoRaster.getGrapImagePlus();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeoreferencingException e) {
			e.printStackTrace();
		}
		
	}
	
	public static LineString toPixel(final GeoRaster geoRaster, final LineString lineString) {
		final Coordinate[] realWorldCoords = lineString.getCoordinates();
		final Coordinate[] pixelGridCoords = new Coordinate[realWorldCoords.length];
		for (int i = 0; i < pixelGridCoords.length; i++) {
			final Point2D p = geoRaster.fromRealWorldCoordToPixelGridCoord(
					realWorldCoords[i].x, realWorldCoords[i].y);
			pixelGridCoords[i] = new Coordinate(p.getX(), p.getY());
		}
		return new GeometryFactory().createLineString(pixelGridCoords);
	}
	
	public static MultiLineString toPixel(final GeoRaster geoRaster, final MultiLineString mls) {

		LineString[] lineStrings = new LineString[mls.getNumGeometries()];
		for (int k = 0; k < mls.getNumGeometries(); k++) {
			LineString ls = (LineString) mls.getGeometryN(k);
			
			lineStrings[k] = toPixel(geoRaster, ls);
		}
		return new GeometryFactory().createMultiLineString(lineStrings);
	}
	
	
	
	
	
	private boolean getSubMatrix3x3(int x, int y, double SubMatrix[]){

		setConstants();
		int	i;
		int iDir;
		float	z, z2;

		boolean result = false;
		try {
			z = grapImagePlus.getPixelValue(x, y);
		

		if(Float.isNaN(z)){
		}
		else{
			//SubMatrix[4]	= 0.0;
			for(i=0; i<4; i++){
				
				iDir = 2 * i;
				z2 = grapImagePlus.getPixelValue(x + m_iOffsetX[iDir], y + m_iOffsetY[iDir]);
				if( !Float.isNaN(z2)){
					SubMatrix[i]	=  z2 - z;
				}
				else{
					z2 = grapImagePlus.getPixelValue(x + m_iOffsetX[(iDir + 4) % 8], y + m_iOffsetY[(iDir  + 4) % 8]);
					if( !Float.isNaN(z2)){
						SubMatrix[i]	= z - z2;
					}
					else{
						SubMatrix[i]	= 0.0;
					}
				}
			}
		

			result= true;
		}
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return result;
		
	}
	
	private void setConstants(){
		
		int i;
		float dCellSize = geoRaster.getMetadata().getPixelSize_X();
		
		double[] m_dDist = new double[8];
		
	    for (i = 0; i < 8; i++){
	        m_dDist[i] = Math.sqrt ( m_iOffsetX[i] * dCellSize * m_iOffsetX[i] * dCellSize
	                        + m_iOffsetY[i] * dCellSize * m_iOffsetY[i] * dCellSize );
	    }
	    
	    _2DX =  dCellSize * 2.0;
	    _6DX = dCellSize * 6.0;
		_DX_2 = dCellSize * dCellSize;
		_4DX_2 = 4.0 * _DX_2;

	}
	
	public double getSlope(int x, int y){

		
		double	zm[], G, H;

		zm = new double[4];
		
		if( getSubMatrix3x3(x, y, zm) ){
			G	=  (zm[0] - zm[2]) / _2DX;
	        H	=  (zm[1] - zm[3]) / _2DX;
	        return Math.atan(Math.sqrt(G*G + H*H));
		}
		else{
			return Double.NaN;
		}
	}
	
	public double getAspect(int x, int y){
		
		double	zm[], G, H, dAspect;

		zm = new double[4];
		
		if( getSubMatrix3x3(x, y, zm) ){
			G	=  (zm[0] - zm[2]) / _2DX;
	        H	=  (zm[1] - zm[3]) / _2DX;
			if( G != 0.0 ){
				dAspect = DEG_180_IN_RAD + Math.atan2(H, G);
			}
			else{
				dAspect = H > 0.0 ? DEG_270_IN_RAD : (H < 0.0 ? DEG_90_IN_RAD : -1.0);
			}
			return dAspect;
		}
		else{
			return Double.NaN;
		}
	}
}
