package gov.usgs.ngwmn.dm.harvest;


import gov.usgs.ngwmn.dm.DataFetcher;
import gov.usgs.ngwmn.dm.cache.Specifier;
import gov.usgs.ngwmn.dm.io.CopyInvoker;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.SupplyInput;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class WebRetriever implements DataFetcher {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected UrlFactory urlFactory = new UrlFactory();
	protected Harvester  harvester  = new Harvester();

	@Override
	public boolean configureInput(Specifier spec, Pipeline pipe)
			throws Exception {
		spec.check();
		
		pipe.setInvoker(new CopyInvoker());
		
		final String url = urlFactory.makeUrl(spec);
		
		if (Strings.isNullOrEmpty(url)) {
			return false;
		}
		
		logger.info("Fetching data for {} from {}", spec, url);

		// TODO set source etc into stats
		pipe.setInputSupplier( new SupplyInput() {
			
			@Override
			public InputStream get() throws IOException {
				// TODO mark start
				int statusCode = harvester.wget(url);
				
		        if (statusCode != HttpStatus.SC_OK) {
		        	// TODO mark fail
		        	throw new IOException("HTTP status error: " + statusCode);
		        }
				return harvester.getInputStream();
				// it's zero, no help here  logger.info("response stream available {}", is.available());
			}
		});
		
		return true;
	}

}
