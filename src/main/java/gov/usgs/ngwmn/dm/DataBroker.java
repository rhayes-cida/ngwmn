package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.cache.EmptyDataFetcher;
import gov.usgs.ngwmn.dm.dao.CacheMetaData;
import gov.usgs.ngwmn.dm.dao.CacheMetaDataDAO;
import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryDAO;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.aggregate.FlowFactory;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataBroker implements FlowFactory, PrefetchI {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	private DataFetcher harvester;
	private DataFetcher retriever;

	private DataLoader  loader;
	
	private WellRegistryDAO wellDAO;
	private CacheMetaDataDAO cacheDAO;
	
	public void fetchWellData(Specifier spec, final Supplier<OutputStream> out) throws Exception {
		Pipeline pipe = (Pipeline) makeFlow(spec, out);
		pipe.invoke();
		logger.info("Completed request operation for {}", spec);
	}
	
	@Override
	public long prefetchWellData(Specifier spec) throws Exception {
		Pipeline pipe = (Pipeline) makeFlow(spec, null);
		long ct = pipe.invoke();
		logger.info("Completed prefetch operation for {}", spec);
		return ct;
	}

	public Pipeline makeFlow(Specifier spec, Supplier<OutputStream> out) throws IOException {	

		check(spec);
		checkSiteExists(spec);
		
		Pipeline pipe    = new Pipeline(spec);
		boolean  success = false;

		// pre-fetch will send in a null output stream
		if (out != null) {
			pipe.setOutputSupplier(out);
			success = configureInput(retriever, pipe);
		}
		
		// check to see if we've tried to fetch but gotten empty results
		// assumes that retriever would have marked success if it found non-empty data in cache
		if ( ! success) {
			success = checkForEmpty(spec, pipe);
		}
		
		// TODO If data type is CONSTRUCTION or LITHOLOGY, look into the log data quality info to see if we have data of the particular sub-type available
		
		if ( ! success) {
			loader.configureOutput(spec, pipe);
			success = configureInput(harvester, pipe); 
		}
		
		return pipe;
	}

	private boolean positive(Integer x) {
		return (x != null) && (x > 0);
	}
	
	public boolean checkForEmpty(Specifier spec, Pipeline pipe)
			throws IOException {
		boolean success = false;
		
		logger.debug("Checking for emptiness of {}", spec);
		WellDataType cachedType = spec.getTypeID().aliasFor;
		if (cachedType != spec.getTypeID()) {
			logger.info("checking for emptiness of aliased type {} from {}", cachedType, spec.getTypeID());
		}
		
		try {
			logger.debug("Updating stats for {}", spec);
			cacheDAO.updateStatsForWell(spec.getWellRegistryKey());
		} catch (Exception e) {
			logger.error("Problem updating stats", e);
		}
		
		CacheMetaData cmd = cacheDAO.get(spec.getWellRegistryKey(), cachedType);
		if (cmd != null) {
			if (positive(cmd.getEmptyCt())) {
				// TODO check for staleness by looking at most recent empty date?
				
				logger.info("returning cached emptiness for {}, most recent empty result on {}", spec, cmd.getMostRecentEmptyDt());
				
				// We tried earlier and found an empty result -- reflect that to the caller
				EmptyDataFetcher edf = new EmptyDataFetcher();
				edf.configureInput(spec, pipe);
				
				success = true;
			} else if (positive(cmd.getFailCt())) {
				// TODO check for staleness by looking at most recent empty date?
				
				logger.info("returning cached failure for {}, most recent failure on {}", spec, cmd.getMostRecentFailDt());
				
				// We tried earlier and only failed -- reflect that to the caller
				EmptyDataFetcher edf = new EmptyDataFetcher();
				edf.configureInput(spec, pipe);
				
				// success in the sense of "we got a result" not "we got data"
				success = true;	
			}
		}
		return success;
	}
	

	public void checkSiteExists(Specifier spec) 
			throws SiteNotFoundException 
	{
		WellRegistryKey wk = spec.getWellRegistryKey();
		WellRegistry well = wellDAO.findByKey(wk);
		
		// TODO Hide the details of the display flag
		boolean exists = (well != null && "1".equals(well.getDisplayFlag()));
		if ( ! exists) {
			throw new SiteNotFoundException(spec);
		}
	}

// TODO this should probably be moved to the harvester or webretriever on begin
//	// TODO It's doubtful if we can detect this until we run the pipe.
//	// TODO We need to distinguish "site not found" and "data not found"
//	if ( ! success) {
//		signalDataNotFoundMsg(pipe);
//	}
//	
//	private void signalDataNotFoundMsg(Pipeline pipe) {
//		Specifier spec = pipe.getSpecifier();
//		logger.warn("No data found for {}", spec);
//		throw new DataNotAvailableException(spec);
//	}
	
	public void setHarvester(DataFetcher harvester) {
		this.harvester = harvester;
	}
	public void setRetriever(DataFetcher retriever) {
		this.retriever = retriever;
	}
	public void setLoader(DataLoader loader) {
		this.loader = loader;
	}
	
	void check(Specifier spec) {
		if (retriever == null && harvester == null) 
			throw new NullPointerException("At least one Data Fetcher is required");
		if (spec == null) 
			throw new NullPointerException("Specifier is required");
	}
	
	boolean configureInput(DataFetcher dataFetcher, Pipeline pipe) throws IOException {
		Specifier spec = pipe.getSpecifier();
		if (dataFetcher != null) {
			boolean v = dataFetcher.configureInput(spec, pipe);
			return v;
		}
		return false;
	}
	
	public void setWellRegistry(WellRegistryDAO wellDAO) {
		this.wellDAO = wellDAO;
	}

	public void setCacheDAO(CacheMetaDataDAO cacheDAO) {
		this.cacheDAO = cacheDAO;
	}


}
