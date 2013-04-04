package gov.usgs.ngwmn.dm.spec;


import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;

import java.security.InvalidParameterException;
import java.util.Date;

import com.google.common.base.Strings;

public class Specifier {
	private final String agencyID;
	private final String featureID;
	private final WellDataType typeID;
	
	private Date beginDate;
	private Date endDate;
	
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((agencyID == null) ? 0 : agencyID.hashCode());
		result = prime * result
				+ ((featureID == null) ? 0 : featureID.hashCode());
		result = prime * result + ((typeID == null) ? 0 : typeID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Specifier other = (Specifier) obj;
		if (agencyID == null) {
			if (other.agencyID != null)
				return false;
		} else if (!agencyID.equals(other.agencyID))
			return false;
		if (featureID == null) {
			if (other.featureID != null)
				return false;
		} else if (!featureID.equals(other.featureID))
			return false;
		if (typeID != other.typeID)
			return false;
		return true;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public boolean isBoundedDates() {
		return beginDate != null || endDate != null;
	}
}

