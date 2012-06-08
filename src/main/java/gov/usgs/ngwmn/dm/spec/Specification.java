package gov.usgs.ngwmn.dm.spec;

import gov.usgs.ngwmn.WellDataType;

import java.util.List;


/**
 * A specifier with potentially more complex request parameters that must be 
 * resolved into individual specifiers
 * 
 * @author david
 *
 */
public class Specification {
	
	private Encoding encode;

	private boolean bundled;
	
	// Delineated list of agency well IDs
	private List<Specifier> wellIDs;
	
	// TODO actual fields required - these are just brainstorm examples.
	// DEC_LAT_VA
	private Double latitudeNorth;
	private Double latitudeSouth;
	// DEC_LONG_VA
	private Double longitudeEast;
	private Double longitudeWest;
	
	// NAT_AQUIFER_CD
	private String aquiferID;
	// AGENCY_CD
	private String agencyAdmin;
	
	// WL_SN_FLAG
	private String wlSnFlag;
	// WL_BASELINE_FLAG
	private String wlBaseFlag;
	// WL_WELL_CHARS
	private String wlChars;
	// WL_WELL_PURPOSE
	private String wlPurpose;
	// WL_WELL_TYPE
	private String wlType;

	// QW_SN_FLAG
	private String qwSnFlag;
	// QW_BASELINE_FLAG
	private String qwBaseFlag;
	// QW_WELL_CHARS
	private String qwChars;
	// QW_WELL_PURPOSE
	private String qwPurpose;
	// QW_WELL_TYPE
	private String qwType;
	
	private WellDataType dataType;
	
	
	public Encoding getEncode() {
		return encode;
	}
	public void setEncode(Encoding encode) {
		this.encode = encode;
	}


	public boolean isBundled() {
		return bundled;
	}
	public void setBundled(boolean bundled) {
		this.bundled = bundled;
	}
	
	
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
	
	
	public String getAgencyAdmin() {
		return agencyAdmin;
	}
	public void setAgencyAdmin(String agencyAdmin) {
		this.agencyAdmin = agencyAdmin;
	}
	
	
	public List<Specifier> getWellIDs() {
		return wellIDs;
	}
	public void setWellIDs(List<Specifier> wellIDs) {
		this.wellIDs = wellIDs;
	}
	public String getAquiferID() {
		return aquiferID;
	}
	public void setAquiferID(String aquiferID) {
		this.aquiferID = aquiferID;
	}
	
	
	public String getWlSnFlag() {
		return wlSnFlag;
	}
	public void setWlSnFlag(String wlSnFlag) {
		this.wlSnFlag = wlSnFlag;
	}
	public String getWlBaseFlag() {
		return wlBaseFlag;
	}
	public void setWlBaseFlag(String wlBaseFlag) {
		this.wlBaseFlag = wlBaseFlag;
	}
	public String getWlChars() {
		return wlChars;
	}
	public void setWlChars(String wlChars) {
		this.wlChars = wlChars;
	}
	public String getWlPurpose() {
		return wlPurpose;
	}
	public void setWlPurpose(String wlPurpose) {
		this.wlPurpose = wlPurpose;
	}
	public String getWlType() {
		return wlType;
	}
	public void setWlType(String wlType) {
		this.wlType = wlType;
	}
	
	
	public String getQwSnFlag() {
		return qwSnFlag;
	}
	public void setQwSnFlag(String qwSnFlag) {
		this.qwSnFlag = qwSnFlag;
	}
	public String getQwBaseFlag() {
		return qwBaseFlag;
	}
	public void setQwBaseFlag(String qwBaseFlag) {
		this.qwBaseFlag = qwBaseFlag;
	}
	public String getQwChars() {
		return qwChars;
	}
	public void setQwChars(String qwChars) {
		this.qwChars = qwChars;
	}
	public String getQwPurpose() {
		return qwPurpose;
	}
	public void setQwPurpose(String qwPurpose) {
		this.qwPurpose = qwPurpose;
	}
	public String getQwType() {
		return qwType;
	}
	public void setQwType(String qwType) {
		this.qwType = qwType;
	}
	
	
	public WellDataType getDataType() {
		return dataType;
	}
	public void setDataType(WellDataType dataType) {
		this.dataType = dataType;
	}
	
	
	public boolean isLatLong() {
		return (latitudeNorth != null
			&&  latitudeSouth != null
			&&  longitudeEast != null
			&&  longitudeWest != null
		);
	}
	
}
