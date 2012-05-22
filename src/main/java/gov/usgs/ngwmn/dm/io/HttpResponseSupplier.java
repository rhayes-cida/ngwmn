package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specification;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseSupplier extends Supplier<OutputStream> {
	
	protected static final int    MAX_BUFFER_SIZE  = 1024<<3; // a reasonable guess at efficiency
	protected static final int    MIN_BUFFER_SIZE  = 2000;   // a reasonable guess at inefficiency
	public    static final String ZIP_CONTENT_TYPE = "application/zip";
	public    static final String XML_CONTENT_TYPE = "text/xml";
	
	private  final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String filename = "data.zip"; // TODO we need a real name
	private HttpServletResponse hsr;
	private Specification spect;
	
	public HttpResponseSupplier(Specification spec, HttpServletResponse response) {
		hsr = response;
		spect = spec;
	}
	
	@Override
	public synchronized OutputStream initialize() throws IOException {
		logger.info("initialize http stream");
		// TODO this is not ideal - not as elegant as the enum solution
		// TODO however it is no longer the data type domain - it is the full request 
		if ( spect.isBundled() || spect.getWellIDs().get(0).getTypeID().contentType.contains("zip") ) {
			hsr.setContentType(ZIP_CONTENT_TYPE);
			// TODO need to name the bundle some how
			logger.debug("send as attachment with file name {}", filename);
			hsr.setHeader("content-disposition", "attachment;filename=" + filename);
		} else {
			hsr.setContentType(XML_CONTENT_TYPE);
		}
		
		// ensure that buffer size is greater than magic lower limit for non-extant sites
		if (hsr.getBufferSize() < MIN_BUFFER_SIZE) {
			hsr.setBufferSize(MAX_BUFFER_SIZE); 
		}
		
		return hsr.getOutputStream();
	}
	
	@Override
	public void end(boolean threw) throws IOException {
		logger.info("closing request stream");
		super.end(threw);
	}

}
