package gov.usgs.ngwmn.dm.cache;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.DataLoader;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

public class Loader 
implements DataLoader {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	private class CacheSavingSupplier extends Supplier<OutputStream> {
		private final Specifier spec;

		private CacheSavingSupplier(Specifier spec) {
			this.spec = spec;
		}

		@Override
		public OutputStream initialize() throws IOException {
			try {
				return makeDestination(spec);
			} catch (IOException ioe) {
				String message = "Problem building output stream for spec " + spec;
				logger.error(message, ioe);
				throw new IOException(message, ioe);
			}
		}
	}

	private Map<WellDataType, Cache> caches;
	
	public OutputStream makeDestination(Specifier spec) 
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
		ImmutableMap.Builder<WellDataType,Cache> builder = ImmutableSortedMap.naturalOrder();		
		for (Cache c : cc) {
			builder.put(c.getDatatype(), c);
		}
		caches = builder.build();
		
		logger.debug("initialized, cache map = {}", caches);
	}
	
	public Map<WellDataType, Cache> getCacheMap() {
		return caches;
	}

	@Override
	public boolean configureOutput(final Specifier spec, Pipeline pipe) throws IOException {
			
		pipe.addOutputSupplier(new CacheSavingSupplier(spec));
		// TODO can inject more outputsuppliers for stats and whatnot
			
		return true;
	}

}
