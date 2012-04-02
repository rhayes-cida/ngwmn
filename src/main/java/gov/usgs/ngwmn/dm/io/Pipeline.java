package gov.usgs.ngwmn.dm.io;


import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatistics.Status;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pipeline {
	private Invoker invoker;
	private InputStream is;
	private OutputStream os;
	private PipeStatistics statistics = new PipeStatistics();
	
	private static Logger logger = LoggerFactory.getLogger(Pipeline.class);
	
	// TODO Consider using com.google.common.io.InputSupplier, OutputSupplier instead
	
	public void setInputStream(InputStream in) {
		is = in;
	}
	
	public void setOutputStream(OutputStream out) {
		os = out;
	}
	public OutputStream getOutputStream() {
		return os;
	}
	
	public void setInvoker(Invoker invoke) {
		invoker = invoke;
	}
	
	// TODO consider implementing Runnable or Callable to make it easy to multithread input for multiple site download
	
	public void invoke() throws IOException {
		statistics.markStart();
		try {
			invoker.invoke(is,os, statistics);
			statistics.markEnd(Status.DONE);
			logger.info("Done stats={}", statistics);
		} catch (IOException ioe) {
			statistics.markEnd(Status.FAIL);
			logger.info("Fail stats={}", statistics);
			throw ioe;
		}
	}
	
	public PipeStatistics getStatistics() {
		return statistics;
	}
}
