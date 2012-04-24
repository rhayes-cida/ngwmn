package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SupplyZipOutput extends Supplier<OutputStream> {
	
	private Supplier<OutputStream> os;
	private ZipOutputStream oz;
	private boolean openEntry;

	public SupplyZipOutput(Supplier<OutputStream> os) {
		this.os = os;
	}

	public OutputStream getOutputStream(Specifier spec) throws IOException {
		if (oz == null && spec != null) {
			// init and chain the stream if not done yet
			openEntry = false;
			oz = new ZipOutputStream( os.get(spec) );
		}
		return oz;
	}
	
	@Override
	public OutputStream get(Specifier spec) throws IOException {
		getOutputStream(spec);
		openEntry(spec);
		return oz;
	}

	private void openEntry(Specifier spec) throws IOException {
		if (!openEntry && spec != null) {
			ZipEntry zip = new ZipEntry( spec.getDualId() );
			oz.putNextEntry(zip);
			openEntry = true;
		}
	}

	private void closeEntry() throws IOException {
		if (openEntry) {
			// if entry is ended then close it
			oz.closeEntry();
			openEntry = false;
		}
	}
	
	@Override
	public void end(Specifier spec) throws IOException {
		if (openEntry) {
			closeEntry();
		} else {
			super.end(spec);
		}
	}
	
	
}
