package gov.usgs.ngwmn.dm;

import gov.usgs.ngwmn.dm.io.Invoker;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ErrorFetcher implements DataFetcher {

	public class ErrorInvoker implements Invoker {

		@Override
		public long invoke(InputStream is, OutputStream os)
				throws IOException {
			throw new IOException("some problem");
		}
	}

	@Override
	public boolean configureInput(Specifier spec, Pipeline pipe)
			throws IOException {
		pipe.setInvoker(new ErrorInvoker());
		return true;
	}

}
