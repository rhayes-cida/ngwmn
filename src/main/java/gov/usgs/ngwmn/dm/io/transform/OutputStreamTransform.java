package gov.usgs.ngwmn.dm.io.transform;


import gov.usgs.ngwmn.dm.io.parse.Element;
import gov.usgs.ngwmn.dm.io.parse.Parser;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class OutputStreamTransform extends FilterOutputStream {

	private static final long DEFAULT_BUFFER_SIZE = 1024<<10; //1mb
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Future<Parser> fparser;
	private PipedInputStream   pin = new PipedInputStream();
	private PipedOutputStream  pout;
	
	private long bytesRecieved;
	private long bytesProcessed;
	private long byteBufferSize;
	
	private Thread parserInit;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	
	public abstract String formatRow(List<Element> headers, Map<String, String> rowData);
	
	public OutputStreamTransform(OutputStream out) throws IOException {
		super(out);
		byteBufferSize = DEFAULT_BUFFER_SIZE;
		pout   = new PipedOutputStream(pin);
	}

	public void setParser(final Parser parser) {
		
		Future<Parser> f = executor.submit(new Callable<Parser>() {
			public Parser call() throws Exception {
	    		logger.debug("InputStream parser setting");
	    		parser.setInputStream(pin);
	    		logger.debug("InputStream parser finished");
				return parser;
			}
		});
		
		fparser = f;
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
    	logger.debug("processing bytes");
    	
    	try {
			Map<String, String> row = fparser.get().nextRow();

			if (row == null) return false;
			
			List<Element> headers   = fparser.get().headers();
			
			if (bytesProcessed == 0) {
				writeRow(headers, null);
			}
			bytesProcessed = fparser.get().bytesParsed();

			writeRow(headers, row);
		} catch (InterruptedException e) {
			throw new IOException(e);
		} catch (ExecutionException e) {
			throw new IOException(e);
		}
    	return true;
	}

    
	private void writeRow(List<Element> headers, Map<String, String> rowData) throws IOException {
		String rowText = formatRow(headers, rowData);
		writeRow(rowText);
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
		logger.debug("finish up processing bytes");
		
		while ( processBytes() );
		
		logger.debug("finished processing cached bytes");
		
		super.flush();
	}
}

