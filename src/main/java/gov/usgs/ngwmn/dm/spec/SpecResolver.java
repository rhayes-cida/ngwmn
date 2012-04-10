package gov.usgs.ngwmn.dm.spec;


public interface SpecResolver  {
	
	Iterable<Specifier> specIterator(Specification spec);
	
}
