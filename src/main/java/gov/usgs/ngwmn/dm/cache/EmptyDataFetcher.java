package gov.usgs.ngwmn.dm.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import gov.usgs.ngwmn.dm.DataFetcher;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

public class EmptyDataFetcher implements DataFetcher {

	@Override
	public boolean configureInput(Specifier spec, Pipeline pipe)
			throws IOException {
		
		byte[] buf = {};
		InputStream eis = new ByteArrayInputStream(buf);
		Supplier<InputStream> eiss = new SimpleSupplier<InputStream>(eis);
		pipe.setInputSupplier(eiss);
		
		return true;
	}

}
