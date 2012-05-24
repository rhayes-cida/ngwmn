package gov.usgs.ngwmn.dm.io.xl;

import java.io.ByteArrayInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for BioShareOutputStreamFilters.
 * 
 * @author srlewein
 *
 */
public abstract class MapOutputStream extends FilterOutputStream {

	private final Log logger = LogFactory.getLog(getClass());
	
	/** The result that is pulled of the outputStream. */
	protected Map<String, Object> dataMap;
	
	/**
	 * Constructor. 
	 * @param outputStream to filter.
	 */
	MapOutputStream(OutputStream out) {
		super(out);
	}
	
	/** 
	 * Checks the byte array that was written. If it is of type Map &lt;String, Object&gt;, set the 
	 * object to be processed. If it is not, the byte array is simply passed to the underlying
	 * outputStream with no intervention.

	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b,off,len));
			Object obj = ois.readObject();
			dataMap = (Map<String, Object>) obj;

		} catch (Exception e) {
			// If there is a problem, just write to the underlying outputStream.
			logger.error("Writing to underlying OutputStream.", e);
			dataMap = null;
			out.write(b, off, len);
			
		}
	}
}
