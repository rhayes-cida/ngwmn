package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.cache.PipeStatistics.Status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TempfileOutputStream extends OutputStream {
	private File endpoint;
	private File tempfile;
	private Status status;
	
	private FileOutputStream delegate;
	
	private static Logger logger = LoggerFactory.getLogger(TempfileOutputStream.class);
	
	public TempfileOutputStream(File ep, File tmp) {
		endpoint = ep;
		tempfile = tmp;

		try {
			delegate = new FileOutputStream(tmp);
		} catch (Exception e) {
			status = Status.FAIL;
			throw new RuntimeException(e);
		}
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void close() throws IOException {
		try {
			delegate.close();
			tempfile.renameTo(endpoint);
			logger.info("Made tempfile permanent for {}", endpoint);
		} catch (IOException e) {
			logger.warn("Problem in closing tempfile for {}", endpoint);
			throw e;
		}
	}

	public void flush() throws IOException {
		try {
			delegate.flush();
		} catch (IOException ioe) {
			logger.warn("Problem in flushing tempfile for {}", endpoint);
			throw ioe;
		}
	}

	public void write(byte[] b, int off, int len) throws IOException {
		try {
			delegate.write(b, off, len);
		} catch (IOException ioe) {
			logger.warn("Problem in write.1 tempfile for {}", endpoint);
			throw ioe;
		}
	}

	public void write(byte[] b) throws IOException {
		try {
			delegate.write(b);
		} catch (IOException ioe) {
			logger.warn("Problem in write.2 tempfile for {}", endpoint);
			throw ioe;
		}
	}

	public void write(int b) throws IOException {
		try {
			delegate.write(b);
		} catch (IOException ioe) {
			logger.warn("Problem in write.3 tempfile for {}", endpoint);
			throw ioe;
		}
	}
}
