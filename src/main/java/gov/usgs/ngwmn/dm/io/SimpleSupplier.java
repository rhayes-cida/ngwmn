package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;


public class SimpleSupplier<T> extends Supplier<T> {

	private final T supply;
	
	public SimpleSupplier(T supply) {
		this.supply = supply;
	}
	
	@Override
	public T get(Specifier spec) throws IOException {
		return supply;
	}

}
