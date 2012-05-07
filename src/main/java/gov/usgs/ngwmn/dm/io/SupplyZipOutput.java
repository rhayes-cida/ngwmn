package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

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
	public OutputStream makeSupply(Specifier spec) throws IOException {
		if (oz != null) return oz;
		
		logger.debug("getOutputStream : making zip output stream");
		// init and chain the stream if not done yet
		oz = new ZipOutputStream( os.begin(spec) );
		return oz;
	}

	/**
	 *  if there is an entry open, close the entry, otherwise we close the stream
	 */
	@Override
	public void end(boolean threw) throws IOException {
			logger.debug("end : closing zip stream");
			super.end(threw);
	}
	
	
	public Supplier<OutputStream> makeEntry(Specifier spec) {
		return new SupplyZipEntry(this, spec);
	}
}
