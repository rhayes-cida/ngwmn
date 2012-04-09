package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.dm.cache.Specifier;

public class SiteNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SiteNotFoundException(String message) {
		super(message);
	}

	public SiteNotFoundException(Specifier spec) {
		super("No site found for " + spec);
	}
	
}
