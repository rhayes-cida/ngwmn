package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;

public interface DataLoader {
	boolean configureOutput(Specifier spec, Pipeline pipe) throws IOException;
}
