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
			ZipEntry zip = new ZipEntry( spec.getAgencyID() + spec.getFeatureID() + "." + spec.getTypeID() );
			oz.putNextEntry(zip);
			openEntry = true;
		}
	}

	private void closeEntry() throws IOException {
		if (openEntry) {
			// only if entry is open then close it
			oz.closeEntry();
			openEntry = false;
		}
	}
	
	/**
	 *  if there is an entry open, close the entry, otherwise we close the stream
	 */
	@Override
	public void end(Specifier spec) throws IOException {
		if (openEntry) {
			closeEntry();
		} else {
			super.end(spec);
		}
	}
	
	
}
