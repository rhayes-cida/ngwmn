package gov.usgs.ngwmn.dm.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.*;


/** Write a copy of every byte we pass thru.
 * 
 * @author rhayes
 *
 */
public class TeeInputStream extends FilterInputStream {

	private OutputStream d1;
	private boolean doClose = false;
	
    private static Logger logger = Logger.getLogger(TeeInputStream.class.getName());

	public TeeInputStream(InputStream is, OutputStream os) {
		super(is);
		this.d1 = os;
	}

	public TeeInputStream(InputStream is, OutputStream os, boolean closeIt) {
		super(is);
		this.d1 = os;
		doClose = closeIt;
	}
	
	@Override
	public int read() throws IOException {
		int v = super.read();
		d1.write(v);
		return v;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int ct = super.read(b);
		if (ct > 0) {
			d1.write(b, 0, ct);
		}
		return ct;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int ct = super.read(b,off,len);
		if (ct > 0) {
			d1.write(b, off, ct);
		}
		return ct;
	}

	@Override
	public void close() throws IOException {
		try {
			d1.flush();
			if (doClose) {
				d1.close();
			}
		} catch (IOException ioe) {
			logger.log(Level.WARNING, "Problem with tee output", ioe);
		}
		super.close();
	}
	
	
}
