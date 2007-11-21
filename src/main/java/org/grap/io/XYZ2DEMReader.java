package org.grap.io;

import java.io.IOException;
import java.net.URL;

import org.grap.model.GeoProcessorType;
import org.grap.model.GrapImagePlus;
import org.grap.model.RasterMetadata;

public class XYZ2DEMReader implements FileReader{

	
	private GeoProcessorType geoProcessorType = GeoProcessorType.FLOAT;

	private String fileName;
	
	//	 constructors
	public XYZ2DEMReader(final String fileName) {
		this(fileName, GeoProcessorType.FLOAT);
	}
	
	
	public XYZ2DEMReader(final String fileName,
			final GeoProcessorType geoProcessorType) {
		this.fileName = fileName;
		this.geoProcessorType = geoProcessorType;
	}

	public XYZ2DEMReader(final URL src) {
		this(src.getFile());
	}
	
	
	public GrapImagePlus readGrapImagePlus() throws IOException, GeoreferencingException {
		
		XYZ2DEM_Importer xyzImporter = new XYZ2DEM_Importer(fileName);
					
		
		return new GrapImagePlus("", xyzImporter.ip);
	}

	public RasterMetadata readRasterMetadata() throws IOException, GeoreferencingException {
		// TODO Auto-generated method stub
		return null;
	}

}
