package gov.usgs.ngwmn.dm.io.parse;

import java.io.IOException;
import java.io.InputStream;
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
	
	// nearest approximation to the number of bytes parsed - the more accurate the better
	// it is used to manage data between PipedInputStream and PipedOutputStream.
	// In order for a StAX reader to be ready to read it must read a few bytes from the XML stream
	long bytesParsed();
	
	// differed setting (removed from constructor) so that the OutputStream can set it on a
	// separate thread - if it was done in the constructor then then FormatingOuputStream cannot
	// receive a parser for polymorphism
	void setInputStream(InputStream is);
	
	boolean done();
}
