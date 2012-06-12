package gov.usgs.ngwmn.dm.cache;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.DataFetcher;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

public class Retriever implements DataFetcher {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private Map<WellDataType, Cache> caches;
	
	public Retriever(Collection<Cache> cc) {
		ImmutableMap.Builder<WellDataType,Cache> builder = ImmutableSortedMap.naturalOrder();		
		for (Cache c : cc) {
			builder.put(c.getDatatype(), c);
		}
		caches = builder.build();
		
		logger.debug("initialized, cache map = {}", caches);
	}

	public Cache getCache(WellDataType t) {
		return caches.get(t);
	}

	@Override
	public boolean configureInput(Specifier spec, Pipeline pipe)
			throws IOException 
	{
		logger.debug("looking for {}", spec);
		Cache cache = getCache(spec.getTypeID());
		if (cache == null) {
			logger.info("no cache for {}", spec);
			return false;
		}
		
		logger.debug("cache for {} is {}", spec, cache);
		if (cache.contains(spec)) {
			logger.info("found cache entry for {}", spec);
			return cache.fetchWellData(spec, pipe); 
		}
		logger.info("cache {} has no entry for {}", cache, spec);
		return false;
	}
	
}
