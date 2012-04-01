package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatisticsWithProblem;

public interface PipelineFinishListener {

	public void notifySuccess(PipeStatistics stats);
	public void notifyException(PipeStatisticsWithProblem pswp);
}
