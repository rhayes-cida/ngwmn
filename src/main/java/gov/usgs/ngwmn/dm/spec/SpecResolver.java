package gov.usgs.ngwmn.dm.spec;

import gov.usgs.ngwmn.WellDataType;


public interface SpecResolver  {
	
	Iterable<Specifier> specIterator(Specification spec, WellDataType type);
	
}
