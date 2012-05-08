package gov.usgs.ngwmn.dm.io;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Invoker {
	/**
	 * Minimal copier. Copies bytes from input to output, returns count.
	 * Should not set status in statistics, leave that for Pipeline.
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	long invoke(InputStream is, OutputStream os) throws IOException;
}
