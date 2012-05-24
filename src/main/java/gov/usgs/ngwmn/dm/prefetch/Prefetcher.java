package gov.usgs.ngwmn.dm.prefetch;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.PrefetchI;
import gov.usgs.ngwmn.dm.dao.CacheMetaData;
import gov.usgs.ngwmn.dm.dao.CacheMetaDataDAO;
import gov.usgs.ngwmn.dm.dao.CacheMetaDataKey;
import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryDAO;
import gov.usgs.ngwmn.dm.spec.Specifier;

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

public class Prefetcher {

	private int fetchLimit = 0;
	
	private Map<String,ExecutorService> agencyExecutorMap = new HashMap<String, ExecutorService>();
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private PrefetchI broker;
	
	private WellRegistryDAO wellDAO;
	private CacheMetaDataDAO cacheDAO;
	
	public int getFetchLimit() {
		return fetchLimit;
	}
	public void setFetchLimit(int fetchLimit) {
		this.fetchLimit = fetchLimit;
	}

	public void setAgencyExecutorMap(Map<String, ExecutorService> agencyExecutorMap) {
		this.agencyExecutorMap = agencyExecutorMap;
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
	
	public void run() {
		int tried = 0;
		
		while (tried < fetchLimit) {
			WellStatus well = nextWell();
			if (well == null) {
				logger.info("Done");
				break;
			}
			
			// TODO check to see if well is marked in appropriate network
			Specifier spec = makeSpec(well.well, well.type);
			
			logger.info("pre-fetch of {}", spec);
			Future<Long> f = dispatch(spec);
						
			try {
				Long ct =  f.get();
				logger.info("pre-fetched {} bytes for {}", ct, spec);
			} catch (Exception x) {
				logger.warn("Failed pre-fetch for " + spec, x);
			}
			tried++;
		}
		
		logger.info("hit fetch limit {}", tried);
	}
	
	private Specifier makeSpec(WellRegistry well, WellDataType wdt) {
		Specifier spec = new Specifier(
				well.getAgencyCd(),
				well.getSiteNo(),
				wdt);
		return spec;
	}

	private Future<Long> dispatch(final Specifier spec) {
		ExecutorService exec = agencyExecutorMap.get(spec.getAgencyID());
		if (exec == null) {
			exec = Executors.newSingleThreadExecutor();
			agencyExecutorMap.put(spec.getAgencyID(), exec);
		}
		
		Future<Long> f = exec.submit(new Callable<Long>() {
			public Long call() throws Exception {
				long count = 
					broker.prefetchWellData(spec);
				return count;
			}
		});
		
		return f;
	}
	
	private static class WellStatus {
		WellRegistry well;
		CacheMetaData cacheInfo;
		WellDataType type;
	}

	PriorityQueue<WellStatus> wellQueue;
	
	private WellStatus nextWell() {
		if (wellQueue == null) {
			wellQueue = populateWellQeue();
		}
		if (wellQueue != null) {
			WellStatus ws = wellQueue.poll();
			if (ws != null) {
				return ws;
			}
		}
		return null;
	}

	private static Comparator<WellStatus> wellCompare = new Comparator<WellStatus>() {

		private int compareDates(Date d1, Date d2) {
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
				v = compareDates(c1.getMostRecentFetchDt(), c2.getMostRecentFetchDt());
				if (v == 0) {
					v = compareDates(c1.getLastDataDt(), c2.getLastDataDt());
				}
				// Could compare other dates etc. etc. etc.
			}
			
			// no fetches recorded -- order by agency, site, type (somewhat arbitrary)
			if (v == 0) {
				v = ws1.well.getAgencyCd().compareTo(ws2.well.getAgencyCd());
			}
			
			if (v == 0) {
				v = ws1.well.getSiteNo().compareTo(ws2.well.getSiteNo());
			}
			
			if (v == 0) {
				v = ws1.type.compareTo(ws2.type);
			}
			
			return v;
		}
		
	};
	
	private PriorityQueue<WellStatus> populateWellQeue() {
		List<WellRegistry> allWells = wellDAO.selectAll();
		PriorityQueue<WellStatus> pq = new PriorityQueue<WellStatus>(allWells.size(), wellCompare);
		
		List<CacheMetaData> cmd = cacheDAO.listAll();
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
			
				pq.add(well);
			}
		}
		
		return pq;
	}
}
