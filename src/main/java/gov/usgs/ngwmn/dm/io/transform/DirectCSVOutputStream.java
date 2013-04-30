package gov.usgs.ngwmn.dm.io.transform;

import gov.usgs.ngwmn.dm.spec.Encoding;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DirectCSVOutputStream 
	extends XSLFilterOutputStream
	implements HeaderWrittenListener, ErrorListener
{

	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	protected boolean writtenHeaders = false;
	protected String agency;
	protected String site;
	protected Date beginDate;
	protected Date endDate;
	protected Encoding encoding;
		
	public DirectCSVOutputStream(String transform, OutputStream out) throws IOException {
		super(out);
		setTransform(transform);
		logger.debug("created");
	}
	
	@Override
	protected void setupTransform(Transformer t) {
		if (getAgency() != null) {
			t.setParameter("agency", getAgency());
		}
		if (getSite() != null) {
			t.setParameter("site",getSite());
		}
		if ( ! isWrittenHeaders()) {
			t.setParameter("emit_header", "true");
		}
		if (getBeginDate() != null) {
			LocalDate dBegin = new LocalDate(getBeginDate());
			t.setParameter("beginDate", dBegin.toString());
		}
		if (getEndDate() != null) {
			LocalDate dEnd = new LocalDate(getEndDate());
			t.setParameter("endDate", dEnd.toString());			
		}

		if (getEncoding() != null) {
			if (getEncoding().getSeparator() != null) {
				t.setParameter("separator", encoding.getSeparator());
			}
		}
		
		logger.debug("Set transform parameters to agency={}, site={}, elevation={}, writtenHeaders={}, begindate{}, endDate={}", 
				new Object[] { getAgency(), getSite(), isWrittenHeaders(), getBeginDate(), getEndDate()});
		
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DirectCSVOutputStream [xform=");
		builder.append(xslHelper.getXformResourceName());
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

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Encoding getEncoding() {
		return encoding;
	}

	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}
	
	

}
