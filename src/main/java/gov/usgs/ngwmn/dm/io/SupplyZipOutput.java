package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

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

	public OutputStream getOutputStream(Specifier spec) throws IOException {
		if (oz == null) { 
			oz = new ZipOutputStream(os.get(spec));
		}
		return oz;
	}
	
	@Override
	public OutputStream get(Specifier spec) throws IOException {
		if (oz == null) {
			getOutputStream(spec);
		} else {
			oz.closeEntry();
		}
		ZipEntry zip = new ZipEntry(spec.getDualId());
		oz.putNextEntry(zip);
		return oz;
	}

}
