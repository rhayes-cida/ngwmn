package gov.usgs.ngwmn.dm.io.transform;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectCSVOutputStream 
	extends XSLFilterOutputStream
	implements HeaderWrittenListener, ErrorListener
{

	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	protected boolean writtenHeaders = false;
	protected String agency;
	protected String site;
	protected Double elevation;
	
	public DirectCSVOutputStream(OutputStream out) throws IOException {
		super(out);
		setTransform("/gov/usgs/ngwmn/wl2csv.xsl");
		logger.debug("created");
	}

	/*
	 * 	<xsl:param name="agency" select="'Agency'" />
		<xsl:param name="site" select="'Site'" />
		<xsl:param name="elevation">0.0</xsl:param>
		<xsl:param name="emit_header" select="false()"/>
	 */
	
	
	@Override
	protected void setupTransform(Transformer t) {
		if (getAgency() != null) {
			t.setParameter("agency", getAgency());
		}
		if (getSite() != null) {
			t.setParameter("site",getSite());
		}
		if (getElevation() != null) {
			t.setParameter("elevation", String.valueOf(getElevation()));
		}
		if ( ! isWrittenHeaders()) {
			t.setParameter("emit_header", "true");
		}
		logger.debug("Set transform parameters to agency={}, site={}, elevation={}, writtenHeaders={}", 
				new Object[] { getAgency(), getSite(), getElevation(), isWrittenHeaders()});
		
		t.setErrorListener(this);
		super.setupTransform(t);
		
		logger.debug("setup Transform {}", t);

	}

	public boolean isWrittenHeaders() {
		return writtenHeaders;
	}

	public void setWrittenHeaders(boolean writtenHeaders) {
		this.writtenHeaders = writtenHeaders;
	}

	@Override
	public void headersWritten() {
		setWrittenHeaders(true);
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DirectCSVOutputStream [xform=");
		builder.append(xformResourceName);
		builder.append(", out=");
		builder.append(out);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void warning(TransformerException exception)
			throws TransformerException {
		logger.warn("transformer warning {}", exception.getMessageAndLocation());
	}

	@Override
	public void error(TransformerException exception)
			throws TransformerException {
		logger.error("transformer error {}", exception.getMessageAndLocation());
	}

	@Override
	public void fatalError(TransformerException exception)
			throws TransformerException {
		logger.error("transformer fatal {}", exception.getMessageAndLocation());		
	}
	
	

}
