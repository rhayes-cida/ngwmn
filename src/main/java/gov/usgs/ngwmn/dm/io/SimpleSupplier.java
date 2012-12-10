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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SimpleSupplier(");
		builder.append(source);
		builder.append(")");
		return builder.toString();
	}
	
	
}
