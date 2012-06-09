package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatistics.Status;
import gov.usgs.ngwmn.dm.spec.Specifier;

public class StatsMaker {

	public static PipeStatistics makeStats(Class<?> clazz) {
		Specifier spec = new Specifier("USGS","007",WellDataType.LOG);
		
		return makeStats(clazz, spec);
	}

	public static PipeStatistics makeStats(Class<?> clazz, Specifier spec) {		
		PipeStatistics stats = new PipeStatistics();
		stats.setCalledBy(clazz);
		stats.setSpecifier(spec);
		stats.setStatus(Status.OPEN);
		stats.markStart();
		return stats;
	}

}
