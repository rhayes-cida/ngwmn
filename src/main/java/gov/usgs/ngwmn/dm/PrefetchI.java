package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.dm.spec.Specifier;

public interface PrefetchI {

	public abstract long prefetchWellData(Specifier spec) throws Exception;

}
