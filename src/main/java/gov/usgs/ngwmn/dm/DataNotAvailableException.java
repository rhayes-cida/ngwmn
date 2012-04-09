package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.dm.spec.Specifier;

public class DataNotAvailableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DataNotAvailableException(String message) {
		super(message);
	}

	public DataNotAvailableException(Specifier spec) {
		super("No data available for " + spec);
	}
	
}
