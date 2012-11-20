package gov.usgs.ngwmn.dm.io.transform;

import java.util.LinkedList;
import java.util.List;

public class HeaderWriterHelper implements HeaderWriter {

	private final List<HeaderWrittenListener> headerListeners;
	private boolean writtenHeaders = false;

	public HeaderWriterHelper() {
		headerListeners = new LinkedList<HeaderWrittenListener>();
	}

	@Override
	public boolean addHeaderListener(HeaderWrittenListener listener) {
		if (listener != null) {
			return headerListeners.add(listener);
		}
		return false;
	}
	
	public void signalHeaderListeners() {
		writtenHeaders=true;

		for (HeaderWrittenListener listener : headerListeners) {
			listener.headersWritten();
		}
	}

	public boolean isWrittenHeaders() {
		return writtenHeaders;
	}

	public void setWrittenHeaders(boolean writtenHeaders) {
		this.writtenHeaders = writtenHeaders;
		if (writtenHeaders) {
			signalHeaderListeners();
		}
	}

	
}
