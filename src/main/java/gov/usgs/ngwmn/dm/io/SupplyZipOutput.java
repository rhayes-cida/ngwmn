package gov.usgs.ngwmn.dm.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SupplyZipOutput extends Supplier<OutputStream> {
	
	private Supplier<OutputStream> os;
	private ZipOutputStream oz;

	public SupplyZipOutput(Supplier<OutputStream> os) {
		this.os = os;
	}

	public OutputStream getOutputStream() throws IOException {
		if (oz == null) { 
			oz = new ZipOutputStream(os.get());
		}
		return oz;
	}
	
	@Override
	public OutputStream get() throws IOException {
		if (oz == null) {
			getOutputStream();
		} else {
			oz.closeEntry();
		}
		ZipEntry zip = new ZipEntry("");
		oz.putNextEntry(zip);
		return oz;
	}

}
