package gov.usgs.ngwmn.dm.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipEntryOutputStream extends FilterOutputStream {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	
	private ZipOutputStream parent;
	private String name;
	
	public ZipEntryOutputStream(ZipOutputStream zip, String entryName) throws IOException {
		super(zip);
		parent = zip;
		name   = entryName;
		ZipEntry entry = new ZipEntry(entryName);
		parent.putNextEntry(entry);
	}
	
	@Override
	public void close() throws IOException {
		logger.trace("closing zip entry {}", name);
		parent.closeEntry();
	}
}