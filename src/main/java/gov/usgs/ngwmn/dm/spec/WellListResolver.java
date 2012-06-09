package gov.usgs.ngwmn.dm.spec;

import gov.usgs.ngwmn.WellDataType;

public class WellListResolver implements SpecResolver {

	@Override
	public Iterable<Specifier> specIterator(Specification spec, WellDataType type) {
		// TODO analyze, slit, and order for grouping
		return spec.getWellIDs(type);
	}

}
