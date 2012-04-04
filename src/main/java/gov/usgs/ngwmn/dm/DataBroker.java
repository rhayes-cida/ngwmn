package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.dm.cache.PipeStatisticsWithProblem;
import gov.usgs.ngwmn.dm.cache.Specifier;
import gov.usgs.ngwmn.dm.io.SupplyOutput;
import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryDAO;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;
import gov.usgs.ngwmn.dm.io.Pipeline;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

public class DataBroker {

	private DataFetcher harvester;
	private DataFetcher retriever;

	private DataLoader  loader;
	
	private WellRegistryDAO wellDAO;
	private EventBus fetchEventBus;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void fetchWellData(Specifier spec, final OutputStream out) throws Exception {
		Pipeline pipe = new Pipeline();

		check(spec);
		
		checkSiteExists(spec);
		
		pipe.setOutputSupplier( new SupplyOutput() {
			@Override
			public OutputStream get() throws IOException {
				return out;
			}
		});
		boolean success = configureInput(retriever, spec, pipe);
		
		
		if ( ! success) {
			loader.configureOutput(spec, pipe);
			success = configureInput(harvester, spec, pipe); 
		}
		
		// TODO It's doubtful if we can detect this until we run the pipe.
		// TODO We need to distinguish "site not found" and "data not found"
		if ( ! success) {
			signalDataNotFoundMsg(spec, pipe);
		}
		
		try {
			pipe.getStatistics().setSpecifier(spec);
			pipe.invoke();
			fetchEventBus.post(pipe.getStatistics());
		} catch (IOException oops) {
			PipeStatisticsWithProblem pswp = new PipeStatisticsWithProblem(pipe.getStatistics(), oops);
			fetchEventBus.post(pswp);
			throw oops;
		}
		
		logger.info("Completed operation for {} result {}", spec, pipe.getStatistics());
	}
	
	private void checkSiteExists(Specifier spec) 
			throws SiteNotFoundException 
	{
		WellRegistryKey wk = spec.getWellRegistryKey();
		WellRegistry well = wellDAO.findByKey(wk.getAgencyCd(),wk.getSiteNo());
		
		// TODO Hide the details of the display flag
		boolean exists = (well != null && "1".equals(well.getDisplayFlag()));
		if ( ! exists) {
			throw new SiteNotFoundException(spec);
		}
	}

	private void signalDataNotFoundMsg(Specifier spec, Pipeline pipe) throws Exception {
		logger.warn("No data found for {}", spec);
		throw new DataNotAvailableException(spec);
	}
	
	public void setHarvester(DataFetcher harvester) {
		this.harvester = harvester;
	}
	public void setRetriever(DataFetcher retriever) {
		this.retriever = retriever;
	}
	public void setLoader(DataLoader loader) {
		this.loader = loader;
	}
	
	void check(Specifier spec) throws Exception {
		if (retriever == null && harvester == null) 
			throw new NullPointerException("At least one Data Fetcher is required");
		if (spec == null) 
			throw new NullPointerException("Specifier is required");
		spec.check();
	}
	
	boolean configureInput(DataFetcher dataFetcher, Specifier spec, Pipeline pipe) throws Exception {
		if (dataFetcher != null) {
			boolean v = dataFetcher.configureInput(spec, pipe);
			pipe.getStatistics().setCalledBy(dataFetcher.getClass());
			return v;
		}
		return false;
	}
	
	// to be replaced with util function - apache commons?
	boolean isEmpty(String string) {
		return string == null || string.length()==0;
	}

	public void setWellRegistry(WellRegistryDAO wellDAO) {
		this.wellDAO = wellDAO;
	}

	public void setFetchEventBus(EventBus fetchEventBus) {
		this.fetchEventBus = fetchEventBus;
	}
	
}
