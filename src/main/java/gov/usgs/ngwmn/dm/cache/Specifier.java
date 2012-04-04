package gov.usgs.ngwmn.dm.cache;


import java.security.InvalidParameterException;

import com.google.common.base.Strings;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;

public class Specifier {
	private String agencyID;
	private String featureID;
	private WellDataType typeID;
	
	public String getAgencyID() {
		return agencyID;
	}
	public void setAgencyID(String agency) {
		this.agencyID = agency;
	}
	
	public String getFeatureID() {
		return featureID;
	}
	public void setFeatureID(String featureID) {
		this.featureID = featureID;
	}
	
	public synchronized WellDataType getTypeID() {
		return typeID;
	}
	
	public void setTypeID(WellDataType typeID) {
		this.typeID = typeID;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Specifier [agencyID=").append(agencyID)
				.append(", featureID=").append(featureID)
				.append(", typeID=").append(typeID).append("]");
		return builder.toString();
	}

	public void check() {
		if ( Strings.isNullOrEmpty(getAgencyID()) ) 
			throw new InvalidParameterException("Well agency Id is required.");
		if ( Strings.isNullOrEmpty(getFeatureID()) ) 
			throw new InvalidParameterException("Well Feature/Site Id is required.");
		if ( getTypeID() == null ) 
			throw new InvalidParameterException("Well data type Id is required.");
	}
	
	public WellRegistryKey getWellRegistryKey() {
		return new WellRegistryKey(agencyID, featureID);
	}
}
