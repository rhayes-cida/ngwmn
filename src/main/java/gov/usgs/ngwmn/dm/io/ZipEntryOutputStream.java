package gov.usgs.ngwmn.dm.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipEntryOutputStream extends FilterOutputStream {
	private ZipOutputStream parent;
	
	public ZipEntryOutputStream(ZipOutputStream zip, String name) throws IOException {
		super(zip);
		parent = zip;
		ZipEntry entry = new ZipEntry(name);
		parent.putNextEntry(entry);
	}
	
	@Override
	public void close() throws IOException {
		parent.closeEntry();
	}
}