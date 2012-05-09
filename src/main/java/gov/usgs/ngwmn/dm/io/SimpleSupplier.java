package gov.usgs.ngwmn.dm.io;


import java.io.Closeable;
import java.io.IOException;


public class SimpleSupplier<T extends Closeable> extends Supplier<T> {

	private final T source; // the stream
	
	public SimpleSupplier(T supply) {
		source = supply;
	}
	
	@Override
	public T initialize() throws IOException {
		return source;
	}

}
