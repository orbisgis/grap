package org.grap.processing.operation.extract;

import java.io.IOException;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

public class ExtractRGBBand {

	
	private GeoRaster geoRaster;
	private ImageStack red;
	private ImageStack green;
	private ImageStack blue;

	public ExtractRGBBand(GeoRaster geoRaster){
		this.geoRaster = geoRaster;
		
	}
	
	public void extractBands(){
		try {
		if (geoRaster.getType()== ImagePlus.COLOR_RGB){
		ImageStack rgb;
		
			rgb = geoRaster.getGrapImagePlus().getStack();
		
		int w = rgb.getWidth();
        int h = rgb.getHeight();
        red = new ImageStack(w,h);
        green = new ImageStack(w,h);
        blue = new ImageStack(w,h);
        
        byte[] r,g,b;
        ColorProcessor cp;
        int slice = 1;
        int n = rgb.getSize();
     
        for (int i=1; i<=n; i++) {
       	 r = new byte[w*h];
            g = new byte[w*h];
            b = new byte[w*h];
            cp = (ColorProcessor)rgb.getProcessor(slice);	             
            cp.getRGB(r,g,b);
            
            red.addSlice(null,r);
            green.addSlice(null,g);
            blue.addSlice(null,b);
        }
		}
        
	
	} catch (IOException e) {
		e.printStackTrace();
	} catch (GeoreferencingException e) {
		e.printStackTrace();
	}
	
	}
	
	public GeoRaster getRedBand(){
		
		return GeoRasterFactory.createGeoRaster(new ImagePlus("red",red),
				geoRaster.getMetadata());
		
	}
	
	public GeoRaster getBlueBand(){
		return GeoRasterFactory.createGeoRaster(new ImagePlus("blue",blue),
				geoRaster.getMetadata());
		
	}
	
	public GeoRaster getGreenBand(){
		return GeoRasterFactory.createGeoRaster(new ImagePlus("green", green),
				geoRaster.getMetadata());
		
	}
}
