package gov.usgs.ngwmn.dm.io;

import java.io.IOException;

public interface Supplier<T> {
	T get() throws IOException;
}
