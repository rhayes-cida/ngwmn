package gov.usgs.ngwmn.dm.io.transform;


import gov.usgs.ngwmn.dm.io.parse.Element;
import gov.usgs.ngwmn.dm.io.parse.Parser;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class OutputStreamTransform extends FilterOutputStream {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private OutputStream    pout;
	private Future<Long>    parserResult;
	private ExecutorService executor;
	private final AtomicReference<List<Element>> headers;
	private LinkedBlockingQueue<Map<String,String>> rows;

	private boolean writtenHeaders;
	
	public abstract String formatRow(List<Element> headers, Map<String, String> rowData);
	
	
	public OutputStreamTransform(OutputStream out) throws IOException {
		super(out);
		executor = Executors.newSingleThreadExecutor();
		headers  = new AtomicReference<List<Element>>();
		rows     = new LinkedBlockingQueue<Map<String,String>>();
	}

	public void setParser(Parser parser) {
		init(parser);
	}
	private void init(final Parser parser) {
		final PipedInputStream pin  = new PipedInputStream();
		try {
			pout = new PipedOutputStream(pin);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		Callable<Long> exec = new Callable<Long>() {
			public Long call() throws Exception {
	    		logger.trace("InputStream parser init started  {}", this);
	    		parser.setInputStream(pin);
	    		logger.trace("InputStream parser init finished {}", this);
	    		
	    		logger.trace("parser started  {}", this);
	    		long count=0;
	    		Map<String,String> row;
				while ( (row=parser.nextRow()) != null ) {
					headers.set( parser.headers() );
					Map<String,String> newRow = new HashMap<String,String>();
					newRow.putAll(row);
					rows.add(newRow);
					count++;
		    		logger.trace("parser row {} ", count);
				}
	    		logger.trace("parser finished {} {}", count, this);
	    		return count;
			}
		};
		parserResult = executor.submit(exec);
	}

	@Override
    public void write(byte b[], int off, int len) throws IOException {
    	pout.write(b, off, len);
    	processRow();
    }

	@Override
    public void write(int b) throws IOException {
    	pout.write(b);
    	processRow();
    }
    
    private void processRow() throws IOException {
		Map<String, String> row = rows.poll();
		if (row != null) {
			List<Element> headList = headers.get();
			if (!writtenHeaders) {
				writeRow(headList);
				writtenHeaders=true;
			}
	    	logger.trace("processing bytes");
			writeRow(headList, row);
		}
	}

    
	private void writeRow(List<Element> headers, Map<String, String> rowData) throws IOException {
		String rowText = formatRow(headers, rowData);
		logger.trace("writeRow: {}", rowText);
		out.write( rowText.toString().getBytes() );
	}

	private void writeRow(List<Element> headers) throws IOException {
		String rowText = formatRow(headers, null); // TODO add method without the row data formatHeaders
		logger.trace("writeRow: {}", rowText);
		out.write( rowText.toString().getBytes() );
	}


	private void finish() throws IOException {
		logger.trace("finish up processing bytes");

    	while ( ! rows.isEmpty() || ! parserResult.isDone() ) {
    		processRow();
    	}
		
		logger.trace("finished processing cached bytes");
	}
	
	@Override
    public void close() throws IOException {
		pout.close(); // this must be done before flushing
		// so that the pin knows that it no longer has to wait for more bytes
		finish();
		out.close();
    }
}

