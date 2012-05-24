package gov.usgs.ngwmn.dm.parse;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamReader;

public abstract class AbstractFormatter implements IFormatter {

	protected MimeType outputType;
	protected Set<MimeType> acceptableOutputTypes;
	private String fileName = DEFAULT_FILE_NAME;

	// ============
	// CONSTRUCTORS
	// ============
	protected AbstractFormatter(MimeType type) {
		this.outputType = type;
	}

	protected AbstractFormatter(String type) {
		this.outputType = MimeType.valueOf(type.toUpperCase());
	}

	// =====================
	// CONFIGURATION METHODS
	// =====================
	@Override
	public void setFileName(String name) {
		if (fileName != null && fileName.length() > 0) {
			this.fileName = name;
		}
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	// ================
	// DISPATCH METHODS
	// ================
	@Override
	public void dispatch(XMLStreamReader in, HttpServletResponse response) throws IOException {
		dispatch(in, response, outputType != MimeType.XML);
	}

	public void dispatch(XMLStreamReader in, HttpServletResponse response, boolean isAttachment) throws IOException {
		String mimeType = outputType.getMimeType();
		response.setContentType(mimeType);
		// we need UTF-8 for xml, turning on for all per IK
		//response.setCharacterEncoding(DEFAULT_ENCODING);
		switch (outputType) {
			case TEXT:
			case EXCEL:
			case KML:
			case CSV:
			case TAB:
			case HTML:
			case JSON:
			case XML:
				if (isAttachment) {
					response.addHeader(
							"Content-Disposition","attachment; filename=" + fileName + "." + outputType.getFileSuffix() );
				}
				break;
			default:
				// xml by default
				if (isAttachment) {
					response.addHeader(
							"Content-Disposition","attachment; filename=" + fileName + "." + MimeType.XML.getFileSuffix() );
				}
		}

		dispatch(in, response.getWriter());
	}

	@Override
	public void dispatch(XMLStreamReader in, OutputStream out) throws IOException {
		dispatch(in, new PrintWriter(out));
	}

	@Override
	public abstract void dispatch(XMLStreamReader in, Writer out) throws IOException;

	// =====================
	// INFORMATIONAL METHODS
	// =====================
	@Override
	public String getMimeType() {
		return outputType.getMimeType();
	}

	@Override
	public String getFileSuffix() {
		return this.outputType.getFileSuffix();
	}

	/**
	 * Returns whether the OutputType is accepted by the IFormatter instance 
	 * @param type
	 * @return true if type accepted
	 * TODO ensure that all subclasses obey this contract. Currently, at least DataFlatteningFormatter does not
	 */
	public boolean accepts(MimeType type) {
		return (acceptableOutputTypes != null) && acceptableOutputTypes.contains(type);
	}



}
