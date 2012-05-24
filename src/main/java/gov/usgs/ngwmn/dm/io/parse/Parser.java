package gov.usgs.ngwmn.dm.io.parse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Parser {
	// returns an ordered list of column headers
	List<Element> headers();
	// returns the map of column values keyed on header
	Map<String, String> currentRow();
	// advances to the next row and returns it as a convenience
	// since a parser is generally going to operate on a stream we might throw an IOExepection
	Map<String, String> nextRow() throws IOException;
}
