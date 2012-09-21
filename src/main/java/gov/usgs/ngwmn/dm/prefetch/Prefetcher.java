package gov.usgs.ngwmn.dm.prefetch;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.PrefetchI;
import gov.usgs.ngwmn.dm.dao.CacheMetaData;
import gov.usgs.ngwmn.dm.dao.CacheMetaDataDAO;
import gov.usgs.ngwmn.dm.dao.CacheMetaDataKey;
import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryDAO;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.InterruptedIOException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Prefetcher implements Callable<PrefetchOutcome> {
	private static final long HOUR = 1000*60*60;

	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	private int fetchLimit = 0;
	private Long timeLimit = null;
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	public synchronized void setThreadCount(int ct) {
		if (executor != null) {
			executor.shutdownNow();
		}
		if (ct < 0) {
			executor = Executors.newCachedThreadPool();
		} else {
			executor = Executors.newFixedThreadPool(ct);
		}
	}
	
	private PrefetchI broker;
	
	private WellRegistryDAO wellDAO;
	private CacheMetaDataDAO cacheDAO;
	
	private PrefetchOutcome outcome = PrefetchOutcome.UNSTARTED;

	public int getFetchLimit() {
		return fetchLimit;
	}
	public void setFetchLimit(int fetchLimit) {
		this.fetchLimit = fetchLimit;
	}

	public Long getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(Long timeLimit) {
		this.timeLimit = timeLimit;
	}
	public void setBroker(PrefetchI broker) {
		this.broker = broker;
	}

	public void setWellDAO(WellRegistryDAO wellDAO) {
		this.wellDAO = wellDAO;
	}

	public void setCacheDAO(CacheMetaDataDAO cacheDAO) {
		this.cacheDAO = cacheDAO;
	}

	private final WellDataType[] fetchTypes = {
			WellDataType.WATERLEVEL,
			WellDataType.QUALITY,
			WellDataType.LOG
	};
	
	public PrefetchOutcome call() {
		
		outcome = PrefetchOutcome.RUNNING;
		
		Iterable<WellStatus> wellQueue = populateWellQeue();
		
		return performPrefetch(wellQueue);
	}
	
	public List<String> agencyCodes() {
		return wellDAO.agencies();
	}
	
	public PrefetchOutcome callForAgency(String agency_cd) {
		
		outcome = PrefetchOutcome.RUNNING;
		
		logger.debug("calling for agency {}", agency_cd);
		Iterable<WellStatus> wellQueue = populateWellQeueForAgency(agency_cd);
		
		return performPrefetch(wellQueue);
	}

	private PrefetchOutcome performPrefetch(Iterable<WellStatus> wellQueue) 
			throws RuntimeException 
	{
		
		int tried = 0;

		if (isQuitting() || Thread.interrupted()) {
			logger.warn("Prefetcher stopped");
			return PrefetchOutcome.INTERRUPTED;
		}
		
		// start timer after the prelimsPrefetcher are done
		Long endTime = null;
		if (timeLimit != null) {
			endTime = System.currentTimeMillis() + timeLimit;
		}
		logger.info("performing prefetch");
		
		for (WellStatus well : wellQueue) {
			MDC.put("well", well.toString());
			try {

				if (isQuitting() || Thread.interrupted()) {
					logger.warn("Prefetch stopped");
					outcome = PrefetchOutcome.LIMIT_TIME;
					break;
				}

				if (fetchLimit > 0 && tried >= fetchLimit) {
					logger.info("hit fetch limit {}", tried);
					outcome = PrefetchOutcome.LIMIT_COUNT;
					break;
				}

				if (endTime != null && System.currentTimeMillis() > endTime) {
					logger.info("hit time limit after {} of {}", tried, fetchLimit);
					outcome = PrefetchOutcome.LIMIT_TIME;
					break;
				}

				// check to see if well is marked in appropriate network
				if (claimsToHaveData(well.well, well.type)) {
					Specifier spec = makeSpec(well.well, well.type);

					logger.debug("pre-fetch of {}", spec);
					Future<Long> f = dispatch(spec);

					try {
						Long ct =  f.get();
						logger.info("pre-fetched {} bytes for {}", ct, spec);
					} catch (Exception x) {
						logger.warn("Failed pre-fetch for " + spec, x);
					}
					tried++;
				} else {
					logger.info("Skipping well {} type {} due to flag", well.well.getMySiteid(), well.type);
				}
			} finally {
				MDC.remove("well");
			}			
		}

		// update stats for other users
		try {
			cacheDAO.updateStatistics();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if (outcome == PrefetchOutcome.RUNNING) {
			outcome = PrefetchOutcome.FINISHED;
		}
		logger.info("Done");
		
		return outcome;
	}
	
	private boolean claimsToHaveData(WellRegistry well, WellDataType dt) {
		switch (dt) {
		case LOG:
			return true; // All wells should provide well log data
		case QUALITY:
			return "1".equals(well.getQwSnFlag());
		case WATERLEVEL:
			return "1".equals(well.getWlSnFlag());
		}
		return true;
	}
	
	private Specifier makeSpec(WellRegistry well, WellDataType wdt) {
		Specifier spec = new Specifier(
				well.getAgencyCd(),
				well.getSiteNo(),
				wdt);
		return spec;
	}
	
	private boolean quitting = false;
	public synchronized void allowRun() {
		logger.info("eanbling prefetch");
		quitting = false;
	}
	public synchronized void requestStop(Exception e) {
		logger.info("requesting stop", e);
		quitting = true;
	}
	private synchronized boolean isQuitting() {
		return quitting;
	}
	
	private Future<Long> dispatch(final Specifier spec) {
		final Map<?, ?> mdc = MDC.getCopyOfContextMap();
		Future<Long> f = executor.submit(new Callable<Long>() {
			public Long call() throws Exception {
				logger.info("in dispatch0 for {}", spec);
				try {
					MDC.setContextMap(mdc);
					try {
						long count = 
								broker.prefetchWellData(spec);
						logger.info("out dispatch0 count{}", count);
						return count;
					}
					catch (InterruptedException ie) {
						logger.warn("out dispatch1", ie);
						requestStop(ie);
						return null;
					}
					catch (InterruptedIOException ioe) {
						logger.warn("out dispatch2", ioe);
						requestStop(ioe);
						return null;
					}
				} finally {
					MDC.clear();
				}
			}
		});
		
		return f;
	}
	
	public static class WellStatus {
		WellRegistry well;
		CacheMetaData cacheInfo;
		WellDataType type;
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("WellStatus[")
					.append("well=").append((well==null)? "??" : well.getMySiteid())
					.append(",type=").append(type)
					.append("]");
			return builder.toString();
		}	
	}

	public Comparator<WellStatus> getWellComparator() {
		return wellCompare;
	}
	
	public Comparator<WellStatus> getSimpleWellComparator() {
		return simpleWellCompare;
	}
	
	private static Comparator<WellStatus> wellCompare = new Comparator<WellStatus>() {

		protected final transient Logger logger = LoggerFactory.getLogger(getClass());

		private int compareDates(Date d1, Date d2) {
			// Null dates default to the beginning of time
			if (d1 == null) {
				d1 = new Date(0);
			}
			if (d2 == null) {
				d2 = new Date(0);
			}
			
			return d1.compareTo(d2);
		}

		// return <0, 0, >0 as o1 is <, =, > o2
		@Override
		public int compare(WellStatus ws1, WellStatus ws2) {
			CacheMetaData c1 = ws1.cacheInfo;
			CacheMetaData c2 = ws2.cacheInfo;
			
			int v = 0;
			
			if (c1 != null && c2 != null) {
				try {
					if (v == 0) {
						v = c1.getFetchPriority().compareTo(c2.getFetchPriority());
					}
					if (v == 0) {
						v = compareDates(c1.getMostRecentAttemptDt(), c2.getMostRecentAttemptDt());
					}
					if (v == 0) {
						// sense reversed, well with more recent data gets re-fetched
						v = compareDates(c2.getLastDataDt(), c1.getLastDataDt());
					}
				} catch (NullPointerException npe) {
					// bail out, this is hopefully a test artifact
					logger.warn("npe in comparator");
				}
				// Could compare other dates etc. etc. etc.
			}
			
			try {
				// no fetches recorded -- order by agency, site, type (somewhat arbitrary)
				if (v == 0) {
					v = ws1.well.getAgencyCd().compareTo(ws2.well.getAgencyCd());
				}
				
				if (v == 0) {
					v = ws1.well.getSiteNo().compareTo(ws2.well.getSiteNo());
				}
			} catch (NullPointerException npe) {
				// bail out, this is hopefully a test artifact
				logger.warn("npe2 in comparator");				
			}
			
			if (v == 0) {
				v = ws1.type.compareTo(ws2.type);
			}
			
			return v;
		}
		
	};
	
	private static Comparator<WellStatus> simpleWellCompare = new Comparator<WellStatus>() {

		protected final transient Logger logger = LoggerFactory.getLogger(getClass());

		private int compareDates(Date d1, Date d2) {
			// Null dates default to the beginning of time
			if (d1 == null) {
				d1 = new Date(0);
			}
			if (d2 == null) {
				d2 = new Date(0);
			}
			
			return d1.compareTo(d2);
		}

		// return <0, 0, >0 as o1 is <, =, > o2
		@Override
		public int compare(WellStatus ws1, WellStatus ws2) {
			CacheMetaData c1 = ws1.cacheInfo;
			CacheMetaData c2 = ws2.cacheInfo;
			
			int v = 0;
			
			if (c1 != null && c2 != null) {
				try {
					if (v == 0) {
						v = compareDates(c1.getMostRecentAttemptDt(), c2.getMostRecentAttemptDt());
					}
					if (v == 0) {
						// sense reversed, well with more recent data gets re-fetched
						v = compareDates(c2.getLastDataDt(), c1.getLastDataDt());
					}
				} catch (NullPointerException npe) {
					// bail out, this is hopefully a test artifact
					logger.warn("npe in comparator");
				}
			}
			
			try {
				// no fetches recorded -- order by agency, site, type (somewhat arbitrary)
				if (v == 0) {
					v = ws1.well.getAgencyCd().compareTo(ws2.well.getAgencyCd());
				}
				
				if (v == 0) {
					v = ws1.well.getSiteNo().compareTo(ws2.well.getSiteNo());
				}
			} catch (NullPointerException npe) {
				// bail out, this is hopefully a test artifact
				logger.warn("npe2 in comparator");				
			}
			
			if (v == 0) {
				v = ws1.type.compareTo(ws2.type);
			}
			
			return v;
		}
		
	};

	private Iterable<WellStatus> populateWellQeue() {
		List<WellRegistry> allWells = wellDAO.selectAll();
		PriorityQueue<WellStatus> pq = new PriorityQueue<WellStatus>(allWells.size(), wellCompare);
		
		// make sure we're working with fresh statistics
		try {
			cacheDAO.updateCacheMetaData();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		List<CacheMetaData> cmd = cacheDAO.listAll();
		
		updateFetchPriorities(cmd);

		Date horizon = new Date(System.currentTimeMillis() - 2*HOUR);
		
		Map<CacheMetaDataKey,CacheMetaData> mdMap = new HashMap<CacheMetaDataKey, CacheMetaData>(cmd.size());
		for (CacheMetaData c : cmd) {
			mdMap.put(c, c);
		}
		
		for (WellRegistry wr : allWells) {
			for (WellDataType dt : fetchTypes) {
				WellStatus well = new WellStatus();
				well.well = wr;
				well.type = dt;
				
				CacheMetaDataKey ck = new CacheMetaDataKey();
				ck.setAgencyCd(wr.getAgencyCd());
				ck.setSiteNo(wr.getSiteNo());
				ck.setDataType(dt.name());
				well.cacheInfo = mdMap.get(ck);
			
				if ( ! tooRecent(well, horizon)) {
					pq.add(well);
				} else {
					logger.info("Skipped {} as too recent", well);
				}
			}
		}
		
		return pq;
	}
	
	private Iterable<WellStatus> populateWellQeueForAgency(String agency_cd) {
		List<WellRegistry> allWells = wellDAO.selectByAgency(agency_cd);
		
		logger.debug("populating well queue with list of {} wells for agency {}", allWells.size(), agency_cd);
		
		PriorityQueue<WellStatus> pq = new PriorityQueue<WellStatus>(allWells.size(), simpleWellCompare);
		
		// make sure we're working with fresh statistics
		try {
			cacheDAO.updateCacheMetaData();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Date horizon = new Date(System.currentTimeMillis() - 2*HOUR);

		List<CacheMetaData> cmd = cacheDAO.listByAgencyCd(agency_cd);
		
		Map<CacheMetaDataKey,CacheMetaData> mdMap = new HashMap<CacheMetaDataKey, CacheMetaData>(cmd.size());
		for (CacheMetaData c : cmd) {
			mdMap.put(c, c);
		}
		
		for (WellRegistry wr : allWells) {
			for (WellDataType dt : fetchTypes) {
				WellStatus well = new WellStatus();
				well.well = wr;
				well.type = dt;
				
				CacheMetaDataKey ck = new CacheMetaDataKey();
				ck.setAgencyCd(wr.getAgencyCd());
				ck.setSiteNo(wr.getSiteNo());
				ck.setDataType(dt.name());
				well.cacheInfo = mdMap.get(ck);
			
				if ( ! tooRecent(well, horizon)) {
					pq.add(well);
				} else {
					logger.info("Skipped {} as fetched too recently", well);
				}
			}
		}
		logger.info("Found {} data streams to fetch for agency {}", pq.size(), agency_cd);
		
		return pq;
	}

	private boolean tooRecent(WellStatus well, Date hzn) {
		try {
			Date lastTry = well.cacheInfo.getMostRecentAttemptDt();
			if (lastTry.after(hzn)) {
				return true;
			}
		} catch (NullPointerException npe) {
			// expected
			return false;
		}
		return false;
	}
	
	/**
	 * Set cache priorities -- this overrides any other ranking.
	 * Lower comes first, default is 100.
	 * @param cmd
	 */
	private void updateFetchPriorities(List<CacheMetaData> cmd) {
		Date now = new Date();
		for (CacheMetaData c : cmd) {
			populateFetchPriority(c, now);
		}
		
	}
	private static final long HOURS = 1000L*60*60;
	private static final long DAYS = HOURS*24;
	
	// try for waterlevel first, log next, then water quality
	private int levelForType(String dt) {
		try {
			WellDataType wdt = WellDataType.valueOf(dt);
			
			switch (wdt) {
			case WATERLEVEL:
				return 1;
			case LOG:
				return 2;
			case QUALITY:
				return 3;
			}
			return 100;
		} catch (IllegalArgumentException iae) {
			logger.warn("unknown well data type {}", dt);
			return 100;
		}
	}
	
	private void populateFetchPriority(CacheMetaData c, Date now) {
		if (c.getMostRecentAttemptDt() == null) {
			c.setFetchPriority(levelForType(c.getDataType()));
		}
		else if (c.getSuccessCt() == 0 && c.getFailCt() > 3) {
			// failures get moved to the end
			// TODO Should re-try every so often even for these
			c.setFetchPriority(200);
		} 
		else if (c.getMostRecentSuccessDt() != null && c.getMostRecentSuccessDt().getTime() < (now.getTime() - 7*DAYS)) {
			// try to fetch every 10 days
			c.setFetchPriority(10);
		}
		else {
			c.setFetchPriority(100);
		}
		
	}
}
