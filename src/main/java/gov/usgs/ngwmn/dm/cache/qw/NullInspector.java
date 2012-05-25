package gov.usgs.ngwmn.dm.cache.qw;

import gov.usgs.ngwmn.WellDataType;

public class NullInspector implements Inspector {

	@Override
	public boolean acceptable(int cachekey) {
		return true;
	}

	@Override
	public WellDataType forDataType() {
		return null;
	}

}
