package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;

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
	 *  for example, Zip Entry management
	 */
	public void end() {
		
	}
}
