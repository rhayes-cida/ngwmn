package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.cache.Specifier;

public interface PipeFactory {
    Pipeline makePipe(Specifier spec);
}
