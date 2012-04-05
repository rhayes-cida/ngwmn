package gov.usgs.ngwmn.dm.io;

import java.io.IOException;

import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

public abstract class Supplier<T> implements InputSupplier<T>, OutputSupplier<T> {
	
	@Override
	public final T getInput() throws IOException {
		return get();
	}
	
	@Override
	public final T getOutput() throws IOException {
		return get();
	}
	
	public abstract T get() throws IOException;
}
