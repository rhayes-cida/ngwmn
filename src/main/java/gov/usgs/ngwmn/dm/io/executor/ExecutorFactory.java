package gov.usgs.ngwmn.dm.io.executor;


import gov.usgs.ngwmn.dm.cache.Specifier;

import java.io.IOException;
import java.io.OutputStream;

public interface ExecutorFactory {
	Executee makeExecutor(Specifier spec, OutputStream out) throws IOException;
}
