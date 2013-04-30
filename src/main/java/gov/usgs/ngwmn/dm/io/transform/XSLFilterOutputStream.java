package gov.usgs.ngwmn.dm.io.transform;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Source;
// import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * On the model of {@link gov.usgs.ngwmn.dm.io.transform.OutputStreamTransform}, 
 * start a Callable that filters the input to the output,
 * passing the input (assumed XML) through the specified XSL transform.
 * 
 * @author rhayes
 *
 */
public class XSLFilterOutputStream extends FilterOutputStream {

	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	public XSLFilterOutputStream(OutputStream out) {
		super(out);
	}
	
	private OutputStream    pout;  // This is sent as input to the transform

	protected Future<Void> xformOutcome;
	private ExecutorService executor;

	protected XSLHelper xslHelper = new XSLHelper();

	public void setExecutor(ExecutorService e) {
		executor = e;
	}

	public void setTransform(Source xform) throws IOException {
		xslHelper.setTransform(xform);
	}
	
	public void setTransform(InputStream xformStream, String xformName) throws IOException  {
		xslHelper.setTransform(xformStream, xformName);
	}
	
	public void setTransform(String xformName) throws IOException {
		xslHelper.setTransform(xformName);
	}
   
	public synchronized void ensureInitialized() {
		if (pout == null) {
			init();
		}
	}
	
	private synchronized void init() {
		final PipedInputStream pin  = new PipedInputStream();
		synchronized (this) {
			try {
				pout = new PipedOutputStream(pin);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		final Map<?,?> mdc = MDC.getCopyOfContextMap();

		Callable<Void> exec = new Callable<Void>() {
			public Void call() throws Exception {
				
				try {
					MDC.setContextMap((mdc == null) ? Collections.emptyMap() : mdc);
	
					Transformer t = xslHelper.getTemplates().newTransformer();
					setupTransform(t);
					StreamResult result = new StreamResult(out);	// this goes to output channel
					
					StreamSource source;
					if (logger.isDebugEnabled()) {
						InputStream sin = new InputStream() {
							long ct = 0;
							
							@Override
							public int read() throws IOException {
								int c = pin.read();
								if (ct == 0) {
									logger.debug("start reading {}", pin);
								}
								if (c < 0) {
									logger.debug("eof for {} at {}", pin, ct);
								} else {
									ct++;
									if (ct < 20) {
										logger.trace("read {} ({}) pos {}", new Object[]{c, (char)c, ct});
									}
								}
								return c;
							}

							@Override
							public void close() throws IOException {
								logger.debug("close for {} at pos {}", pin, ct);
								super.close();
							}
							
						};
						source = new StreamSource(sin);	
					} else {
						source = new StreamSource(pin);	// this is piped from write methods
					}
					
					t.transform(source, result);
					logger.debug("transform done with {}", pin);
				} catch (Exception e) {
					logger.warn("Problem in transform by " + xslHelper.getXformResourceName(), e);
					throw e;
				}
				return null;
			}
		};
		
		xformOutcome = executor.submit(exec);

	}
	
	/**
	 * Do whatever you need to do to initialize the transform (like set parameters).
	 * @param t
	 */
	protected void setupTransform(Transformer t) {
		// This space available
	}

	private void finish() throws IOException {
    	try {
    		xformOutcome.get(100, TimeUnit.SECONDS);
    		logger.debug("done with XSL");
    	} catch (Exception e) {
    		logger.warn("Problem encountered in finish", e);
    	}
		
		logger.trace("finished XSL processing");
	}

	@Override
    public void close() throws IOException {
		try {
			logger.trace("closing pout");
			pout.close(); // this triggers XSL completion
			finish();
			logger.debug("close event, out={}", out);
			// out.close();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e);
		}
    }

	@Override
	public void write(int b) throws IOException {
		ensureInitialized();
		pout.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		ensureInitialized();
		pout.write(b, off, len);
	}

    
}
