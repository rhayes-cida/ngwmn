package gov.usgs.ngwmn.dm.io;

import java.io.IOException;
import java.io.OutputStream;


public class SupplyTeeOutput extends Supplier<OutputStream> {

	private Supplier<OutputStream> os1;
	private Supplier<OutputStream> os2;
	
	public SupplyTeeOutput(Supplier<OutputStream> os1, Supplier<OutputStream> os2) {
		this.os1 = os1;
		this.os2 = os2;
	}
	
	@Override
	public OutputStream get() throws IOException {
		return new TeeOutputStream(os1.get(), os2.get());
	}

}
