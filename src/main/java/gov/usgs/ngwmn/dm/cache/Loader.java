package gov.usgs.ngwmn.dm.cache;

import gov.usgs.ngwmn.dm.DataLoader;
import gov.usgs.ngwmn.dm.io.Pipeline;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loader 
implements DataLoader {

	private Cache cache;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public OutputStream destination(Specifier well) 
			throws IOException
	{
		return cache.destination(well);
	}

	public Cache getCache() {
		return cache;
	}

	public Loader(Cache c) {
		super();
		this.cache = c;
	}

	@Override
	public boolean configureOutput(Specifier spec, Pipeline pipe) throws Exception {
		try {
			pipe.addOutputStream( destination(spec) );
			// TODO can inject more outputstreams for stats and whatnot
		} catch (IOException ioe) {
			String message = "Problem building output stream for spec " + spec;
			logger.error(message, ioe);
			throw new RuntimeException(message, ioe);
		}
		return true;
	}

}
