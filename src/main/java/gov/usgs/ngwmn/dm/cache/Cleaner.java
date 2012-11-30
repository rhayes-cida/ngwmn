package gov.usgs.ngwmn.dm.cache;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cleaner {
	
	// initialize everything to safe values
	private List<Cache> toClean = Collections.emptyList();
	private int daysToKeep = Integer.MAX_VALUE;
	private int countToKeep = Integer.MAX_VALUE;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public List<Cache> getToClean() {
		return toClean;
	}

	public void setToClean(List<Cache> toClean) {
		this.toClean = toClean;
	}

	public int getDaysToKeep() {
		return daysToKeep;
	}

	public void setDaysToKeep(int daysToKeep) {
		this.daysToKeep = daysToKeep;
	}

	public int getCountToKeep() {
		return countToKeep;
	}

	public void setCountToKeep(int countToKeep) {
		this.countToKeep = countToKeep;
	}
	
	public int clean() {
		logger.debug("Cleaning {}", toClean);
		int ct = 0;
		for (Cache c : toClean) {
			int clnd = c.cleanCache(daysToKeep, countToKeep);
			logger.debug("Cleaned {} from {}", clnd, c);
			ct += clnd;
		}
		return ct;
	}

}
