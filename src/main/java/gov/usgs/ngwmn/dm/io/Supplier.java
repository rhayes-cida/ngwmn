package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.Closeable;
import java.io.IOException;

import com.google.common.io.Closeables;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

public abstract class Supplier<T extends Closeable> implements InputSupplier<T>, OutputSupplier<T> {
	
	private Specifier defaultSpec;
	private T supply;
	
	public Specifier getDefaultSpec() {
		return defaultSpec;
	}

	public void setDefaultSpec(Specifier defaultSpec) {
		this.defaultSpec = defaultSpec;
	}

	@Override
	public final T getInput() throws IOException {
		return begin(defaultSpec);
	}
	
	@Override
	public final T getOutput() throws IOException {
		return begin(defaultSpec);
	}
	
	public final T begin(Specifier spec) throws IOException {
		// cannot do a null bypass because zip supply needs to make a new entry
		// each impl must be smart about its makeSupply
		supply = makeSupply(spec);
		return supply;
	}
	
	public abstract T makeSupply(Specifier spec) throws IOException;
	
	
	/**
	 *  signal the end of a stream for those suppliers that 
	 *  need to clean up after each source. 
	 *  
	 *  for example, Zip Entry management will need to close an entry
	 *  and all streams will need to closed eventually the streams
	 */
	public void end(boolean threw) throws IOException {
		// TODO maybe the default behavior could be to close the supplied stream
		// TODO this way the specific impl may be able to 
		// TODO this might not be the best but will be fleshed out in time
		Closeables.close( supply, threw );
	}
	
	
	public Supplier<T> makeEntry(Specifier spec) {
		return this;
	}

}
