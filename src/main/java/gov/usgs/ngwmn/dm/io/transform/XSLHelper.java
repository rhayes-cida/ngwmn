package gov.usgs.ngwmn.dm.io.transform;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XSLHelper {
	private String xformResourceName;
	private Templates templates;

	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	public XSLHelper() {
	}

	private Templates loadXSLT(Source xslSource)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException
	{
		try {
	
			TransformerFactory transFact = TransformerFactory.newInstance();
			// Can get more details by implementing ErrorListener transFact.setErrorListener(listener);
			logger.debug("Transformer factory class is {}", transFact.getClass());
			Templates templates = transFact.newTemplates(xslSource);
	
			return templates;
		} catch (TransformerConfigurationException tce) {
			logger.error("Problem loading templates from " + xslSource.getSystemId(), tce);
			logger.error("java.home is {}", System.getProperty("java.home", "unknown"));
			throw tce;
		}
	}

	private Templates loadXSLT(InputStream xin, String xslName)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException {
		Source xslSource = new StreamSource(xin);
		if (null == xslSource.getSystemId()) {
			xslSource.setSystemId(xslName);
		}
		return loadXSLT(xslSource);
	}

	Templates loadXSLT(String xsltResource) 
			throws TransformerConfigurationException, IOException 
	{
		InputStream xin = getClass().getResourceAsStream(xsltResource);
		try {
			return loadXSLT(xin, xsltResource);
		} finally {
			xin.close();
		}
	}

	public void setTransform(Source xform) throws IOException {
		xformResourceName = xform.toString();
		try {
			templates = loadXSLT(xform);	
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public void setTransform(InputStream xformStream, String xformName) throws IOException  {
		xformResourceName = xformName;
		try {
			templates = loadXSLT(xformStream,xformName);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public void setTransform(String xformName) throws IOException {
		xformResourceName = xformName;
		try {
			templates = loadXSLT(xformName);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public Templates getTemplates() {
		return templates;
	}

	public String getXformResourceName() {
		return xformResourceName;
	}
	
}