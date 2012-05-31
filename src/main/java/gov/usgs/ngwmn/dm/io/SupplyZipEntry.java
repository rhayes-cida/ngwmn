package gov.usgs.ngwmn.dm.io;

import java.io.IOException;
import java.io.OutputStream;

public class SupplyZipEntry extends Supplier<OutputStream> {

	private EntryName entryName;
	private SupplyZipOutput parent;
	
	public SupplyZipEntry(SupplyZipOutput supplyZipOutput, EntryName name) {
		parent = supplyZipOutput;
		entryName = name;
	}

	@Override
	public ZipEntryOutputStream initialize() throws IOException {
			
		String name = entryName.name();
		logger.debug("initialize : zip entry {}", name);
		
		return new ZipEntryOutputStream( parent.getZip(), entryName.name() );
	}
	
}
