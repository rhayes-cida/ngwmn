package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.fs.FileCache;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FakeInputInvoker implements Invoker {

	private Specifier specifier;
	public FakeInputInvoker(Specifier spec) {
		specifier = spec;
	}

	@Override
	public long invoke(InputStream is, OutputStream os, PipeStatistics stats)
			throws IOException {
		String s = String.valueOf(specifier);
		
		InputStream fakeInput = new ByteArrayInputStream(s.getBytes());
		
		return FileCache.copyStream(fakeInput, os, stats);

	}

}
