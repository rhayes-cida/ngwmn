package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatisticsWithProblem;
import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryDAO;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.executor.ExecFactory;
import gov.usgs.ngwmn.dm.io.executor.Executee;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

public class DataBroker implements ExecFactory {

	private DataFetcher harvester;
	private DataFetcher retriever;

	private DataLoader  loader;
	
	private WellRegistryDAO wellDAO;
	private EventBus fetchEventBus;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void fetchWellData(Specifier spec, final Supplier<OutputStream> out) throws Exception {
		Pipeline pipe = (Pipeline) makeExecutor(spec, out);
		invokePipe(pipe);
		logger.info("Completed request operation for {} result {}", spec, pipe.getStatistics());
	}
	
	public PipeStatistics prefetchWellData(Specifier spec) throws Exception {
		Pipeline pipe = (Pipeline) makeExecutor(spec, null);
		invokePipe(pipe);
		logger.info("Completed prefetch operation for {} result {}", spec, pipe.getStatistics());
		return pipe.getStatistics();
	}

	private void invokePipe(Pipeline pipe) throws IOException {
		try {
			pipe.invoke();
			fetchEventBus.post(pipe.getStatistics());
		} catch (IOException oops) {
			PipeStatisticsWithProblem pswp = new PipeStatisticsWithProblem(pipe.getStatistics(), oops);
			fetchEventBus.post(pswp);
			throw oops;
		}
	}
	
	public Executee makeExecutor(Specifier spec, final Supplier<OutputStream> out) throws IOException {	

		check(spec);
		checkSiteExists(spec);
		
		Pipeline pipe    = new Pipeline(spec);
		boolean  success = false;

		pipe.getStatistics().setSpecifier(spec);
		
		// pre-fetch will send in a null output stream
		if (out != null) {
			pipe.setOutputSupplier( new Supplier<OutputStream>() {
				@Override
				public OutputStream get(Specifier spec) throws IOException {
					return out.get(spec);
				}
			});
			success = configureInput(retriever, pipe);
		}
		
		if ( ! success) {
			loader.configureOutput(spec, pipe);
			success = configureInput(harvester, pipe); 
		}
		
		// TODO It's doubtful if we can detect this until we run the pipe.
		// TODO We need to distinguish "site not found" and "data not found"
		if ( ! success) {
			signalDataNotFoundMsg(pipe);
		}
		
		return pipe;
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

	private void signalDataNotFoundMsg(Pipeline pipe) {
		Specifier spec = pipe.getSpecifier();
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
	
	void check(Specifier spec) {
		if (retriever == null && harvester == null) 
			throw new NullPointerException("At least one Data Fetcher is required");
		if (spec == null) 
			throw new NullPointerException("Specifier is required");
		spec.check();
	}
	
	boolean configureInput(DataFetcher dataFetcher, Pipeline pipe) throws IOException {
		Specifier spec = pipe.getSpecifier();
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
