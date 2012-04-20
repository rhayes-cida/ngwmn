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
	private boolean endEntry;

	public SupplyZipOutput(Supplier<OutputStream> os) {
		this.os = os;
	}

	public OutputStream getOutputStream(Specifier spec) throws IOException {
		if (oz == null && spec != null) {
			// init and chain the stream if not done yet
			openEntry = endEntry = false;
			oz = new ZipOutputStream( os.get(spec) );
		}
		return oz;
	}
	
	@Override
	public OutputStream get(Specifier spec) throws IOException {
		getOutputStream(spec);
		closeEntry();
		openEntry(spec);
		return oz;
	}

	private void closeEntry() throws IOException {
		if (endEntry) {
			// if entry is ended then close it
			openEntry = endEntry = false;
			oz.closeEntry();
		}
	}

	private void openEntry(Specifier spec) throws IOException {
		if (!openEntry && spec != null) {
			ZipEntry zip = new ZipEntry( spec.getDualId() );
			oz.putNextEntry(zip);
			openEntry = true;
		}
	}

	@Override
	public void end() {
		super.end(); // just in case there is impl there
		endEntry = true; // record that we received an end signal
	}
	
	
}
