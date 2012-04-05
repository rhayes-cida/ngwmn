package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.Specifier;

public class StatsMaker {

	public static PipeStatistics makeStats(Class<?> clazz) {
		Specifier spec = new Specifier();
		spec.setAgencyID("USGS");
		spec.setFeatureID("007");
		spec.setTypeID(WellDataType.ALL);
		
		PipeStatistics stats = new PipeStatistics();
		stats.setCalledBy(clazz);
		stats.setSpecifier(spec);
		stats.markStart();
		return stats;
	}

}
