package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.Closeable;
import java.io.IOException;


public class SimpleSupplier<T extends Closeable> extends Supplier<T> {

	private final T supply; // the stream
	
	public SimpleSupplier(T supply) {
		this.supply = supply;
	}
	
	@Override
	public T makeSupply(Specifier spec) throws IOException {
		return supply;
	}

}
