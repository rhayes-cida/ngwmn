package gov.usgs.ngwmn.dm.io;


import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SupplyZipOutput extends Supplier<OutputStream> {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
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
	
	
	public Supplier<OutputStream> makeEntry(EntryName entryName) {
		return new SupplyZipEntry(this, entryName);
	}
	
	protected ZipOutputStream getZip() {
		if (oz == null) {
			throw new NullPointerException("call to getZip prior to source initialization.");
		}
		return oz;
	}
}
