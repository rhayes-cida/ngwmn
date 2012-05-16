package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryDAO;
import gov.usgs.ngwmn.dm.dao.WellRegistryKey;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.executor.FlowFactory;
import gov.usgs.ngwmn.dm.io.executor.Flow;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataBroker implements FlowFactory {

	private DataFetcher harvester;
	private DataFetcher retriever;

	private DataLoader  loader;
	
	private WellRegistryDAO wellDAO;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void fetchWellData(Specifier spec, final Supplier<OutputStream> out) throws Exception {
		Pipeline pipe = (Pipeline) makeFlow(spec, out);
		invokePipe(pipe);
		logger.info("Completed request operation for {}", spec);
	}
	
	public long prefetchWellData(Specifier spec) throws Exception {
		Pipeline pipe = (Pipeline) makeFlow(spec, null);
		long ct = invokePipe(pipe);
		logger.info("Completed prefetch operation for {}", spec);
		return ct;
	}

	private long invokePipe(Pipeline pipe) throws IOException {
		return pipe.invoke();
	}
	
	public Flow makeFlow(Specifier spec, Supplier<OutputStream> out) throws IOException {	

		check(spec);
		checkSiteExists(spec);
		
		Pipeline pipe    = new Pipeline(spec);
		boolean  success = false;

		// pre-fetch will send in a null output stream
		if (out != null) {
			pipe.setOutputSupplier(out);
			success = configureInput(retriever, pipe);
		}
		
		if ( ! success) {
			loader.configureOutput(spec, pipe);
			success = configureInput(harvester, pipe); 
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


}
