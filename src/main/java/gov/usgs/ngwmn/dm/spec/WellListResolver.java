package gov.usgs.ngwmn.dm.spec;

public class WellListResolver implements SpecResolver {

	@Override
	public Iterable<Specifier> specIterator(Specification spec) {
		// TODO analyze, slit, and order for grouping
		return spec.getWellIDs();
	}

}
