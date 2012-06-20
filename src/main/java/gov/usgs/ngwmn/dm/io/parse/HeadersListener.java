package gov.usgs.ngwmn.dm.io.parse;

import java.util.List;

public interface HeadersListener {
	void headerUpdate(List<Element> headers);
}
