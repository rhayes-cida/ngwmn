package gov.usgs.ngwmn.dm.io;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.InputSupplier;

public abstract class SupplyInput implements InputSupplier<InputStream>, Supplier<InputStream> {

	@Override
	public final InputStream getInput() throws IOException {
		return get();
	}
}
