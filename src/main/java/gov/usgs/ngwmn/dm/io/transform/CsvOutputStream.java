package gov.usgs.ngwmn.dm.io.transform;

import gov.usgs.ngwmn.dm.io.parse.Element;
import gov.usgs.ngwmn.dm.io.parse.Parser;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CsvOutputStream extends FilterOutputStream implements OutputStreamTransform {

	private static final long DEFAULT_BUFFER_SIZE = 1024<<10; //1mb
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Parser parser;
	private PipedInputStream   pin = new PipedInputStream();
	private PipedOutputStream  pout;
	
	private long bytesRecieved;
	private long bytesProcessed;
	private long byteBufferSize;
	
	private Thread parserInit;
	
	public CsvOutputStream(OutputStream out) throws IOException {
		super(out);
		byteBufferSize = DEFAULT_BUFFER_SIZE;
		pout   = new PipedOutputStream(pin);
	}

	@Override
	public void setParser(final Parser parser) {
		this.parser = parser;
		
		parserInit = new Thread(
			new Runnable(){
		        public void run(){
		        	synchronized (CsvOutputStream.this) {
			    		logger.debug("CSV started  constructing parser");
			    		parser.setInputStream(pin);
			    		logger.debug("CSV finished constructing parser");
			    		parserInit = null; // indicate the parser is finished
					}
		        }
		    },
		"CSV Parser Init - " + RandomUtils.nextInt(9999)); // looks like same name threads is a problem
		parserInit.setDaemon(true); // if something goes wrong allow jvm to exit
		parserInit.start();
	}
	
	public void setBufferSize(long size) {
		byteBufferSize = size;
	}
	

    public void write(int b) throws IOException {
    	bytesRecieved++;
    	pout.write(b);

    	// TODO if the parser was running on its own thread to get the next row
    	// TODO we would not need this byte count caching
    	// If we have cached enough then process a row
    	if (bytesRecieved - bytesProcessed > byteBufferSize) {
    		processBytes();
    	}
    }
    
    private synchronized boolean processBytes() throws IOException {
    	logger.debug("CSV processing bytes");
    	
    	ensureParserReady();
    	
    	Map<String, String> row = parser.nextRow();

    	if (row == null) return false;
		
    	List<Element> headers   = parser.headers();
    	
    	if (bytesProcessed == 0) {
    		writeRow(headers, null);
    	}
    	bytesProcessed = parser.bytesParsed();

    	writeRow(headers, row);
    	return true;
	}

	private void ensureParserReady() {
		if (parserInit != null) { // if parser is not finished
    		try {
    			logger.debug("CSV waiting on parser init");
    			// lets make sure the parser is read to process bytes
    			parserInit.join();
    		} catch (Exception e) {
    			// even though we null check the thread we cannot be sure that it might finish between check and join
    			logger.info("NullPointer or Interrupted Exceptions are not a problem for parser init");
    			// log this occurrence in case it happens too much and we want to re-fator
    		}
    	}
	}

	private void writeRow(List<Element> headers, Map<String, String> rowData) throws IOException {
		String rowText = formatRow(headers, rowData);
		writeRow(rowText);
	}

	public String formatRow(List<Element> headers, Map<String, String> rowData) {
		StringBuilder rowText = new StringBuilder(); 
		
		for (Element header : headers) {
			String data = (rowData==null) ? header.displayName : rowData.get(header.fullName);
			data = (data==null) ? "" : data;
			rowText.append(data).append(',');
		}
		return rowText.toString();
	}

	public void writeRow(String rowText) throws IOException {
		out.write(rowText.toString().getBytes(), 0, rowText.length()-1);
		out.write("\n".getBytes());
		
		// I tried using a writer but the text did not make it to the os
		//writer.write(rowText.toString(), 0, rowText.length()-1);
		//writer.write("\n");
	}

	@Override
    public void close() throws IOException {
		pout.close(); // this must be done before flushing
		// so that the pin knows that it no longer has to wait for more bytes
		flush();
		pin.close();
		out.close();
    }

	@Override
	public void flush() throws IOException {
		logger.debug("CSV finish up processing bytes");
		
		while ( processBytes() );
		
		logger.debug("CSV finished processing cached bytes");
		
		super.flush();
	}
}
