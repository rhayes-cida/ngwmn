package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.dm.cache.Specifier;
import gov.usgs.ngwmn.dm.io.SupplyOutput;
import gov.usgs.ngwmn.dm.io.Pipeline;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataBroker {

	private DataFetcher harvester;
	private DataFetcher retriever;

	private DataLoader  loader;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void fetchWellData(Specifier spec, final OutputStream out) throws Exception {
		Pipeline pipe = new Pipeline();

		check(spec);
		
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
		pipe.invoke();
		
		// TODO Temporary measure, in lieu of a more authoritative check per Well_Registry
		if (pipe.getStatistics().getCount() < 2000) {
			throw new SiteNotFoundException(spec);
		}
		logger.info("Completed operation for {} result {}", spec, pipe.getStatistics());
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
}
