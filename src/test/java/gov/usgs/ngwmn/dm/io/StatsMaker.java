package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.spec.Specifier;

public class StatsMaker {

	public static PipeStatistics makeStats(Class<?> clazz) {
		Specifier spec = new Specifier("USGS","007",WellDataType.ALL);
		
		return makeStats(clazz, spec);
	}

	public static PipeStatistics makeStats(Class<?> clazz, Specifier spec) {		
		PipeStatistics stats = new PipeStatistics();
		stats.setCalledBy(clazz);
		stats.setSpecifier(spec);
		stats.markStart();
		return stats;
	}

}
