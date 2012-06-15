package gov.usgs.ngwmn.dm.io;


import java.io.Closeable;
import java.io.IOException;


public class JoiningSupplier<T extends Closeable> extends Supplier<T> {

	private T source; // the stream
	private Supplier<T> supplier;

	public JoiningSupplier(T supply) {
		source = supply;
	}
	public JoiningSupplier(Supplier<T> supplier) {
		this.supplier = supplier;
	}
	
	@Override
	public T initialize() throws IOException {
		if (source==null && supplier!=null) {
			if (supplier.isInitialized()) {
				source = supplier.getSource();
			} else {
				source = supplier.begin();
			}
		}
		if (source==null) {
			throw new RuntimeException("Failed to get wrapped stream");
		}
		return source;
	}
	
	@Override
	public void end(boolean threw) throws IOException {
		supplier.end(threw);
	}
	
	@Override
	public Supplier<T> makeEntry(EntryDescription entryDesc) {
		if (supplier==null) {
			return this;
		}
		return supplier.makeEntry(entryDesc);
	}

}
