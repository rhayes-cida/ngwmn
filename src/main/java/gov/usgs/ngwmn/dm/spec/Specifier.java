package gov.usgs.ngwmn.dm.spec;


import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;

import java.security.InvalidParameterException;

import com.google.common.base.Strings;

public class Specifier {
	private final String agencyID;
	private final String featureID;
	private final WellDataType typeID;
	
	
	public Specifier(String agencyID, String featureID, WellDataType typeID) {
		this.agencyID = agencyID;
		this.featureID = featureID;
		this.typeID = typeID;
		check();
	}
	
	public Specifier(WellRegistry well, WellDataType typeID) {
		this( well.getAgencyCd(), well.getSiteNo(), typeID );
	}
	
	public String getAgencyID() {
		return agencyID;
	}
	
	public String getFeatureID() {
		return featureID;
	}
	
	public synchronized WellDataType getTypeID() {
		return typeID;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Specifier [agencyID=").append(agencyID)
				.append(", featureID=").append(featureID)
				.append(", typeID=").append(typeID).append("]");
		return builder.toString();
	}

	private void check() {
		if ( Strings.isNullOrEmpty(agencyID) ) 
			throw new InvalidParameterException("Well agency Id is required.");
		if ( Strings.isNullOrEmpty(featureID) ) 
			throw new InvalidParameterException("Well Feature/Site Id is required.");
		if ( typeID == null ) 
			throw new InvalidParameterException("Well data type Id is required.");
	}

	public WellRegistryKey getWellRegistryKey() {
		return new WellRegistryKey(agencyID, featureID);
	}
}

