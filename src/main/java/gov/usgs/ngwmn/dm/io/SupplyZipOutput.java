package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SupplyZipOutput extends Supplier<OutputStream> {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Supplier<OutputStream> os;
	private ZipOutputStream oz;
	private boolean openEntry;

	public SupplyZipOutput(Supplier<OutputStream> os) {
		this.os = os;
	}

	private OutputStream getOutputStream(Specifier spec) throws IOException {
		if (oz == null) {
			logger.warn("getOutputStream : making zip output stream");
			// init and chain the stream if not done yet
			openEntry = false;
			oz = new ZipOutputStream( os.begin(spec) );
		}
		return oz;
	}
	
	@Override
	public OutputStream makeSupply(Specifier spec) throws IOException {
//		logger.warn("makeSupply : making zip output stream");
		getOutputStream(spec);
		openEntry(spec);
		return oz;
	}

	private void openEntry(Specifier spec) throws IOException {
		if (!openEntry && spec != null) {
			logger.warn("openEntry : making zip entry {}", spec);
			
			ZipEntry zip = new ZipEntry( spec.getAgencyID() + spec.getFeatureID() + "." + spec.getTypeID() );
			oz.putNextEntry(zip);
			openEntry = true;
		}
	}

	private void closeEntry() throws IOException {
		if (openEntry) {
			logger.warn("closeEntry : closing zip entry");
			// only if entry is open then close it
			oz.closeEntry();
			openEntry = false;
		}
	}
	
	/**
	 *  if there is an entry open, close the entry, otherwise we close the stream
	 */
	@Override
	public void end(boolean threw) throws IOException {
		if (openEntry) {
			//logger.warn("end : closing zip entry");

			try {
				closeEntry();
			} catch (IOException e) {
				if ( ! threw ) {
					throw e;
				}
			}
		} else {
			logger.warn("end : closing zip stream");
			super.end(threw);
		}
	}
	
	
}
