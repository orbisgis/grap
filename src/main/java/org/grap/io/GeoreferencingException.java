package org.grap.io;

/**
 * Indicates that the raster could not be georeferenced. This can mean that the
 * world file is missing or it has invalid information
 * 
 * @author Fernando Gonzalez Cortes
 * 
 */
public class GeoreferencingException extends Exception {
	public GeoreferencingException(final String message) {
		super(message);
	}
}