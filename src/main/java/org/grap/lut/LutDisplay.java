package org.grap.lut;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;

public class LutDisplay {

	
	private ColorModel cm;

	public LutDisplay(ColorModel cm) {
		
		this.cm = cm;
		
		
	}
	
	public ImagePlus getImagePlus(){
				

		int w = 256, h, x, y, j;
		h=20;
		
				ImagePlus imp = NewImage.createByteImage("Lut", w, h, 1, 0);
				ImageProcessor ip = imp.getProcessor();
				byte[] pixels = (byte[])ip.getPixels();
				
					
				
				j=0;
				for (y = 0; y < h; y++) {
					for (x = 0; x < w; x++) {
						pixels[j++] = (byte) x;
						
					}
				}
				
				
				imp.getProcessor().setColorModel(cm);
				
				imp.updateAndDraw();
				
				
				
				return imp;
							
	}
	
	
}
