package org.grap.lut;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;

public class LutDisplay {

	
	private ColorModel cm;

	public LutDisplay(ColorModel cm) {
		
		cm = this.cm;
		
		
	}
	
	public ImagePlus getImage(){
		
		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];

		int w = 256, h, x, y, i, j;
		h=20;
		
				ImagePlus imp = NewImage.createByteImage("Lut", w, h, 1, 0);
				ImageProcessor ip = imp.getProcessor();
				byte[] pixels = (byte[])ip.getPixels();
				imp.show();
				j=0;
				for (y = 0; y < h; y++) {
					for (x = 0; x < w; x++) {
						pixels[j++] = (byte) x;
					}
				}
				imp.getProcessor().setColorModel(cm);
				imp.getProcessor().resize(1, 20);
				imp.updateAndDraw();
				
				
				return imp;
							
	}
	
	
}
