package gov.usgs.ngwmn.dm.io;


import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

public abstract class Supplier<T extends Closeable> implements InputSupplier<T>, OutputSupplier<T> {
	
	private T source;
	protected transient Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public final T getInput() throws IOException {
		return begin();
	}
	
	@Override
	public final T getOutput() throws IOException {
		return begin();
	}
	
	public synchronized final T begin() throws IOException {

		if ( isInitialized() ) {
			throw new IOException("Supplier must be initiated only once.");
		}
		
		source = initialize();
		
		logger.info("Subclass created a stream {}", source);
		
		return source;
	}
	
	public boolean isInitialized() {
		return source != null;
	}
	
	public abstract T initialize() throws IOException;
	
	
	/**
	 *  signal the end of a stream for those suppliers that 
	 *  need to clean up after each source. 
	 *  
	 *  for example, Zip Entry management will need to close an entry
	 *  and all streams will need to closed eventually the streams
	 */
	public void end(boolean threw) throws IOException {
		if (source == null) {
			throw new IOException("call to end prior to source begin.");
		}
		Closeables.close(source, threw);
		
		// once closed it is no longer initialized
		source = null; // this is also useful for multiple item bundling
	}
	
	
	public Supplier<T> makeEntry(EntryDescription entryDesc) {
		return this;
	}
	
	
	// this is for primarily for testing and might be able to be protected
	// alternatively we could make the source attribute protected
	protected T getSource() {
		if ( ! isInitialized() ) {
			throw new NullPointerException("call to getSource prior to initialization.");
		}
		return source;
	}
}
