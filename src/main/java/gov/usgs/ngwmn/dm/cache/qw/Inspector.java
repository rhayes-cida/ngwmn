package gov.usgs.ngwmn.dm.cache.qw;

import gov.usgs.ngwmn.WellDataType;

public interface Inspector {

	public boolean acceptable(int cachekey) throws Exception;
	
	public WellDataType forDataType();
}
