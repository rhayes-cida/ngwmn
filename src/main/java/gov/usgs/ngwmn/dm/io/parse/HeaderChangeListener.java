package gov.usgs.ngwmn.dm.io.parse;

import java.util.List;

public interface HeaderChangeListener {
	void headersChanged(List<Element> headers);
}
