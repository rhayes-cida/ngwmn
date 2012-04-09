package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatisticsWithProblem;
import gov.usgs.ngwmn.dm.dao.FetchLog;
import gov.usgs.ngwmn.dm.dao.FetchLogDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

public class FetchRecorder {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private FetchLogDAO dao;
	
	@Subscribe
	public void notifySuccess(PipeStatistics stats) {
		notify(stats, null);
	}

	@Subscribe
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
		
		logger.error("asdf 1");
		dao.insertId(item);
		logger.error("asdf 2");
		
		return item;
	}

	public void setDao(FetchLogDAO dao) {
		this.dao = dao;
	}


}
