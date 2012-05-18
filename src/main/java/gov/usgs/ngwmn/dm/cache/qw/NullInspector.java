package gov.usgs.ngwmn.dm.cache.qw;

public class NullInspector implements Inspector {

	@Override
	public boolean acceptable(int cachekey) {
		return true;
	}

}
