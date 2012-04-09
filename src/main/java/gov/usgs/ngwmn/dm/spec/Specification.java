package gov.usgs.ngwmn.dm.spec;


/**
 * A specifier with potentially more complex request parameters that must be 
 * resolved into individual specifiers
 * 
 * @author david
 *
 */
public class Specification extends Specifier {

	// TODO actual fields required - these are just brainstorm examples.
	private long latitudeNorth;
	private long latitudeSouth;
	private long longitudeEast;
	private long longitudeWest;
	
	public long getLatitudeNorth() {
		return latitudeNorth;
	}
	public void setLatitudeNorth(long latitudeNorth) {
		this.latitudeNorth = latitudeNorth;
	}
	public long getLatitudeSouth() {
		return latitudeSouth;
	}
	public void setLatitudeSouth(long latitudeSouth) {
		this.latitudeSouth = latitudeSouth;
	}
	public long getLongitudeEast() {
		return longitudeEast;
	}
	public void setLongitudeEast(long longitudeEast) {
		this.longitudeEast = longitudeEast;
	}
	public long getLongitudeWest() {
		return longitudeWest;
	}
	public void setLongitudeWest(long longitudeWest) {
		this.longitudeWest = longitudeWest;
	}
}
