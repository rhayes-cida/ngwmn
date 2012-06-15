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
		// TODO this was to allow for concatenation
		// TODO it seems it might be handled with a JoiningSupplier
		// TODO the test suite does not call with parent requiring init
		// TODO in other words this might be dead code
		if ( ! parent.isInitialized() ) {
			parent.begin();
		}
		
		String name = entryDesc.entryName();
		logger.debug("initialize : zip entry {}", name);
		
		return new ZipEntryOutputStream( parent.getZip(), name );
	}
	
}
