package gov.usgs.ngwmn.dm.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatisticsWithProblem;
import gov.usgs.ngwmn.dm.dao.FetchLog;
import gov.usgs.ngwmn.dm.dao.FetchLogDAO;
import gov.usgs.ngwmn.dm.spec.Specifier;

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

	@Subscribe
	public void updateLog(FetchLog fl) {
		dao.update(fl);
	}
	
	public void notifyException(PipeStatistics stats, Throwable problem) {
		notify(stats, problem);
	}

	private FetchLog notify(PipeStatistics stats, Throwable problem) {
		
		FetchLog item = new FetchLog();
		Specifier specifier = stats.getSpecifier();
		if (specifier != null) {
			item.setWell(specifier.getWellRegistryKey());
			item.setDataStream(specifier.getTypeID().toString());
			item.setSpecifier(specifier.toString());			
		} else {
			item.setAgencyCd("");
			item.setSiteNo("");
		}
		
		item.setElapsedSec(stats.getElapsedTime());
		item.setStartedAt(stats.getStartDate());
		item.setCt(stats.getCount());
		if (stats.getCalledBy() != null) {
			item.setFetcher(stats.getCalledBy().getSimpleName());
		}
		item.setSource(stats.getSource());
		if (stats.getStatus() != null) {
			item.setStatus(stats.getStatus().as4Char());
		}
		if (problem != null) {
			item.setProblem(problem.toString());
		}
		
		dao.insertId(item);
		
		logger.debug("recorded fetch log {} for specifier {}", item, stats.getSpecifier());
		stats.setFetchLog(item);
		
		return item;
	}

	public void setDao(FetchLogDAO dao) {
		this.dao = dao;
	}


}
