package gov.usgs.ngwmn.dm.cache;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.DataLoader;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loader 
implements DataLoader {

	private Map<WellDataType, Cache> caches;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public OutputStream destination(Specifier spec) 
			throws IOException
	{
		Cache cache = caches.get(spec.getTypeID());
		if (cache == null) {
			throw new RuntimeException("No cache configured for " + spec.getTypeID());
		}
		return cache.destination(spec);
	}

	public Cache getCache(WellDataType t) {
		return caches.get(t);
	}

	public Loader(Collection<Cache> cc) {
		caches = new HashMap<WellDataType, Cache>(WellDataType.values().length);
		for (Cache c : cc) {
			caches.put(c.getDatatype(), c);
		}
	}

	@Override
	public boolean configureOutput(final Specifier spec, Pipeline pipe) throws IOException {
			
		pipe.addOutputSupplier( new Supplier<OutputStream>() {				
			@Override
			public OutputStream initialize() throws IOException {
				try {
					return Loader.this.destination(spec);
				} catch (IOException ioe) {
					String message = "Problem building output stream for spec " + spec;
					logger.error(message, ioe);
					throw new IOException(message, ioe);
				}
			}
		});
		// TODO can inject more outputsuppliers for stats and whatnot
			
		return true;
	}

}
