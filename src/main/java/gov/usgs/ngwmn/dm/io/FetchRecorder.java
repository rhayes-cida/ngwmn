package gov.usgs.ngwmn.dm.io;

import com.google.common.eventbus.Subscribe;

import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatisticsWithProblem;
import gov.usgs.ngwmn.dm.dao.FetchLog;
import gov.usgs.ngwmn.dm.dao.FetchLogDAO;

public class FetchRecorder implements PipelineFinishListener {

	private FetchLogDAO dao;
	
	@Subscribe
	@Override
	public void notifySuccess(PipeStatistics stats) {
		notify(stats, null);
	}

	@Subscribe
	@Override
	public void notifyException(PipeStatisticsWithProblem pswp) {
		notify(pswp.getStats(), pswp.getProblem());		
	}

	public void notifyException(PipeStatistics stats, Throwable problem) {
		notify(stats, problem);
	}

	private FetchLog notify(PipeStatistics stats, Throwable problem) {
		FetchLog item = new FetchLog();
		item.setWell(stats.getSpecifier().getWellRegistryKey());
		item.setElapsedSec(stats.getElapsedTime());
		item.setStartedAt(stats.getStartDate());
		item.setFetcher(stats.getCalledBy().getSimpleName());
		item.setSource(stats.getSource());
		item.setSpecifier(stats.getSpecifier().toString());
		item.setStatus(stats.getStatus().as4Char());
		if (problem != null) {
			item.setProblem(problem.toString());
		}
		
		dao.insertId(item);
		
		return item;
	}

	public void setDao(FetchLogDAO dao) {
		this.dao = dao;
	}


}
