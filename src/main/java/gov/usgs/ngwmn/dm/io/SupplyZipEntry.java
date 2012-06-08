package gov.usgs.ngwmn.dm.io;

import java.io.IOException;
import java.io.OutputStream;

public class SupplyZipEntry extends Supplier<OutputStream> {

	private EntryDescription entryDesc;
	private SupplyZipOutput parent;
	
	public SupplyZipEntry(SupplyZipOutput supplyZipOutput, EntryDescription name) {
		parent = supplyZipOutput;
		entryDesc = name;
	}

	@Override
	public ZipEntryOutputStream initialize() throws IOException {
		// TODO this allows for concatenation - it might be better to handle this with a JoiningSupplier
		if ( ! parent.isInitialized() ) {
			parent.begin();
		}
		
		String name = entryDesc.entryName();
		logger.debug("initialize : zip entry {}", name);
		
		return new ZipEntryOutputStream( parent.getZip(), name );
	}
	
}
