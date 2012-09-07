package gov.usgs.ngwmn.dm.cache;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.io.NullOutputStream;

public class NullCache implements Cache {

	private WellDataType wdt;
	
	public NullCache(WellDataType wdt) {
		this.wdt = wdt;
	}

	@Override
	public OutputStream destination(Specifier well) throws IOException {
		return new NullOutputStream();
	}

	@Override
	public boolean fetchWellData(Specifier spec, Pipeline pipe)
			throws IOException {
		return false;
	}

	@Override
	public boolean contains(Specifier spec) {
		return false;
	}

	@Override
	public CacheInfo getInfo(Specifier spec) {
		return null;
	}

	@Override
	public WellDataType getDatatype() {
		return wdt;
	}

	@Override
	public InputStream retrieve(String id) throws IOException {
		throw new RuntimeException("the retrieve method is not implemented and should not be called");
	}

	@Override
	public int cleanCache(int daysToRetain, int countToRetain) {
		return 0;
	}

}
