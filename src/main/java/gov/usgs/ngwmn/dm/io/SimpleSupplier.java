package gov.usgs.ngwmn.dm.io;


import java.io.Closeable;
import java.io.IOException;


public class SimpleSupplier<T extends Closeable> extends Supplier<T> {

	private T source; // the stream

	public SimpleSupplier(T supply) {
		if (supply==null) {
			throw new RuntimeException("Source supply is required");
		}
		source = supply;
	}
	
	@Override
	public T initialize() throws IOException {
		return source;
	}

	// had added this while working out multisite joining aggregation - it might not be needed
//	@Override
//	public void end(boolean threw) throws IOException {
//		// should do nothing because the wrapping class opened this stream
//	}

}
