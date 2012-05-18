package gov.usgs.ngwmn.dm.cache.qw;

public interface Inspector {

	public boolean acceptable(int cachekey) throws Exception ;
}
