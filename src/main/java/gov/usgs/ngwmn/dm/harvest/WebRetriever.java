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
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected UrlFactory urlFactory = new UrlFactory();
	protected Harvester  harvester  = new Harvester();

	@Override
	public boolean configureInput(Specifier spec, final Pipeline pipe) throws IOException {
		
		pipe.setInvoker(new CopyInvoker());
		
		final String url = urlFactory.makeUrl(spec);
		
		if ( Strings.isNullOrEmpty(url) ) {
			return false;
		}
		
		logger.info("Fetching data for {} from {}", spec, url);
		pipe.getStatistics().setSource(url);

		pipe.setInputSupplier( new Supplier<InputStream>() {
			InputStream is;
			
			@Override
			public InputStream makeSupply(Specifier spec) throws IOException {
				if (is!=null) return is;
				
				// TODO did not expect this behavior. seems out of place
				pipe.getStatistics().markStart();  
				
				int statusCode = harvester.wget(url);
				
		        if (statusCode != HttpStatus.SC_OK) {
		        	pipe.getStatistics().markEnd(Status.FAIL);
		        	IOException ioe = new IOException("HTTP status error: " + statusCode +" for spec " + spec);
		        	pipe.setException(ioe);
		        	throw ioe;
		        }
		        is = harvester.getInputStream();
				return is;
				// it's zero, no help here  logger.info("response stream available {}", is.available());
			}
		});
		
		return true;
	}

}
