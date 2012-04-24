package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.Closeable;
import java.io.IOException;

import com.google.common.io.Closeables;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

public abstract class Supplier<T> implements InputSupplier<T>, OutputSupplier<T> {
	
	private Specifier defaultSpec;
	
	public Specifier getDefaultSpec() {
		return defaultSpec;
	}

	public void setDefaultSpec(Specifier defaultSpec) {
		this.defaultSpec = defaultSpec;
	}

	@Override
	public final T getInput() throws IOException {
		return get(defaultSpec);
	}
	
	@Override
	public final T getOutput() throws IOException {
		return get(defaultSpec);
	}
	
	public abstract T get(Specifier spec) throws IOException;
	
	/**
	 *  signal the end of a stream for those suppliers that 
	 *  need to clean up after each source. 
	 *  
	 *  for example, Zip Entry management will need to close an entry
	 *  and all streams will need to closed eventually the streams
	 */
	public void end(Specifier spec) {
		// TODO maybe the default behavior could be to close the supplied stream
		// TODO this way the specific impl may be able to 
		// TODO this might not be the best but will be fleshed out in time
		try {
			if (get(spec)!=null) {
				Closeables.closeQuietly( (Closeable)get(spec) );
			}
		} catch (IOException e) {
			// TODO this is just a place holder for now to fix a test immediately
			// TODO the exception will be thrown once the full refactor is impl
		}
	}
}
