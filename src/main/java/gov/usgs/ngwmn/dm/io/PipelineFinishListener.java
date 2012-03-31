package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.cache.PipeStatistics;

public interface PipelineFinishListener {

	public void notifySuccess(PipeStatistics stats);
	public void notifyException(PipeStatistics stats, Throwable problem);
}
