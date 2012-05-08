package gov.usgs.ngwmn.dm.harvest;


import gov.usgs.ngwmn.dm.DataFetcher;
import gov.usgs.ngwmn.dm.cache.PipeStatistics.Status;
import gov.usgs.ngwmn.dm.io.CopyInvoker;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class WebRetriever implements DataFetcher {
	
	public static class WebInputSupplier extends Supplier<InputStream> {
		private final Pipeline pipe;
		private final String url;
		private final Harvester h;

		InputStream is;

		public WebInputSupplier(Pipeline pipe, String url, Harvester harv) {
			this.pipe = pipe;
			this.url = url;
			this.h = harv;
		}
		
		public String getUrl() {
			return url;
		}
		public Pipeline getPipeline() {
			return pipe;
		}

		@Override
		public InputStream makeSupply(Specifier spec) throws IOException {
			if (is!=null) return is;
			
			// TODO did not expect this behavior. seems out of place
			pipe.getStatistics().markStart();  
			
			int statusCode = h.wget(url);
			
		    if (statusCode != HttpStatus.SC_OK) {
		    	pipe.getStatistics().markEnd(Status.FAIL);
		    	IOException ioe = new IOException("HTTP status error: " + statusCode +" for spec " + spec);
		    	pipe.setException(ioe);
		    	throw ioe;
		    }
		    is = h.getInputStream();
			return is;
			// it's zero, no help here  logger.info("response stream available {}", is.available());
		}
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected UrlFactory urlFactory = new UrlFactory();
	protected Harvester  harvester  = new Harvester();

	@Override
	public boolean configureInput(final Specifier spec, final Pipeline pipe) throws IOException {
		
		pipe.setInvoker(new CopyInvoker());
		
		final String url = urlFactory.makeUrl(spec);
		
		if ( Strings.isNullOrEmpty(url) ) {
			return false;
		}
		
		logger.info("Fetching data for {} from {}", spec, url);
		pipe.getStatistics().setSource(url);

		pipe.setInputSupplier(new WebInputSupplier(pipe, url, harvester));
		
		return true;
	}

}
