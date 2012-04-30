package gov.usgs.ngwmn.dm.io.executor;

import java.util.concurrent.Callable;


public interface Executee extends Callable<Void> {
	//void close() throws IOException;
}
