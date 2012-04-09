package gov.usgs.ngwmn.dm.io.executor;


import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;

public interface ExecFactory {
	Executee makeExecutor(Specifier spec, OutputStream out) throws IOException;
}
