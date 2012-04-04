package gov.usgs.ngwmn.dm.io;

import java.io.IOException;
import java.io.OutputStream;

import com.google.common.io.OutputSupplier;

public abstract class SupplyOutput implements OutputSupplier<OutputStream>, Supplier<OutputStream> {

	@Override
	public final OutputStream getOutput() throws IOException {
		return get();
	}
}
