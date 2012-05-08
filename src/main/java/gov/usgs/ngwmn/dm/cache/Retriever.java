package gov.usgs.ngwmn.dm.cache;

import gov.usgs.ngwmn.dm.DataFetcher;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;

public class Retriever implements DataFetcher {
	private Cache cache;
	
	public Retriever(Cache c) {
		this.cache = c;
	}

	@Override
	public boolean configureInput(Specifier spec, Pipeline pipe)
			throws IOException 
	{
		if (cache.contains(spec)) {
			// pipe.getStatistics().setCalledBy(this.getClass());
			return cache.fetchWellData(spec, pipe); 
		}
		return false;
	}
	
}
