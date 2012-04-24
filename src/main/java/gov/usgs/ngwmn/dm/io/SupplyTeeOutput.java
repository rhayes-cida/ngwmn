package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;


public class SupplyTeeOutput extends Supplier<OutputStream> {

	private Supplier<OutputStream> os1;
	private Supplier<OutputStream> os2;
	private OutputStream os;
	
	public SupplyTeeOutput(Supplier<OutputStream> os1, Supplier<OutputStream> os2) {
		this.os1 = os1;
		this.os2 = os2;
	}
	
	@Override
	public OutputStream get(Specifier spec) throws IOException {
		if (os==null) {
			os = new TeeOutputStream(os1.get(spec), os2.get(spec));
		}
		return os;
	}
	
	@Override
	public void end(Specifier spec) throws IOException {
		try {
			os1.end(spec);
		} finally {
			os2.end(spec);
		}
	}

}
