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
	private Double latitudeNorth;
	private Double latitudeSouth;
	private Double longitudeEast;
	private Double longitudeWest;
	
	private String aquiferId;
	private String agencyAdmin;
	private String wlClass;
	private String qwClass;
		
	public Double getLatitudeNorth() {
		return latitudeNorth;
	}
	public void setLatitudeNorth(double latitudeNorth) {
		this.latitudeNorth = latitudeNorth;
	}
	public Double getLatitudeSouth() {
		return latitudeSouth;
	}
	public void setLatitudeSouth(double latitudeSouth) {
		this.latitudeSouth = latitudeSouth;
	}
	public Double getLongitudeEast() {
		return longitudeEast;
	}
	public void setLongitudeEast(double longitudeEast) {
		this.longitudeEast = longitudeEast;
	}
	public Double getLongitudeWest() {
		return longitudeWest;
	}
	public void setLongitudeWest(double longitudeWest) {
		this.longitudeWest = longitudeWest;
	}
	
	
	public String getAquiferId() {
		return aquiferId;
	}
	public void setAquiferId(String aquiferId) {
		this.aquiferId = aquiferId;
	}
	public String getAgencyAdmin() {
		return agencyAdmin;
	}
	public void setAgencyAdmin(String agencyAdmin) {
		this.agencyAdmin = agencyAdmin;
	}
	public String getWlClass() {
		return wlClass;
	}
	public void setWlClass(String wlClass) {
		this.wlClass = wlClass;
	}
	public String getQwClass() {
		return qwClass;
	}
	public void setQwClass(String qwClass) {
		this.qwClass = qwClass;
	}
	
	
	public boolean isLatLong() {
		return (latitudeNorth != null
			&&  latitudeSouth != null
			&&  longitudeEast != null
			&&  longitudeWest != null
		);
	}
	
}
