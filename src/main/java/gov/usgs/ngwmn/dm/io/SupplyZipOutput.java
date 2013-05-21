package gov.usgs.ngwmn.dm.io;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;


public class SupplyZipOutput extends Supplier<OutputStream> {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	
	private Supplier<OutputStream> os;
	private ZipOutputStream oz;
	
	public SupplyZipOutput(Supplier<OutputStream> os) {
		this.os = os;
	}

	
	@Override
	public OutputStream initialize() throws IOException {
		logger.debug("initialize : zip output stream");
		return oz = new ZipOutputStream( os.begin() );
	}

	/**
	 *  if there is an entry open, close the entry, otherwise we close the stream
	 */
	@Override
	public void end(boolean threw) throws IOException {
		logger.debug("end : closing zip stream");
		super.end(threw);
	}
	
	
	public Supplier<OutputStream> makeEntry(EntryDescription entryDesc) {
		return new SupplyZipEntry(this, entryDesc);
	}
	
	protected ZipOutputStream getZip() {
		// TODO the test suites never call this with oz==null - it might be dead code
		if (oz == null) {
			throw new NullPointerException("call to getZip prior to source initialization.");
		}
		return oz;
	}


	public void addStream(String name, String mimeType, InputStream dd) throws IOException {
		ZipOutputStream zip = getZip();
		
		ZipEntryOutputStream zos = new ZipEntryOutputStream(zip, name);
		try {
			ByteStreams.copy(dd, zos);
		} finally {
			zos.close();
		}
	}
}
