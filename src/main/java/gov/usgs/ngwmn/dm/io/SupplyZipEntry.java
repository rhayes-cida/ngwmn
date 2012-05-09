package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;

public class SupplyZipEntry extends Supplier<OutputStream> {

	private Specifier spec;
	private SupplyZipOutput parent;
	
	public SupplyZipEntry(SupplyZipOutput supplyZipOutput, Specifier specifier) {
		spec   = specifier;
		parent = supplyZipOutput;
	}

	@Override
	public ZipEntryOutputStream initialize() throws IOException {
			
		String name = new StringBuilder()
					.append(spec.getAgencyID())
					.append('_')
					.append(spec.getFeatureID()) 
					.append('_')
					.append(spec.getTypeID())
					.append('.')
					.append(spec.getTypeID().suffix)
					.toString();
		
		return new ZipEntryOutputStream( parent.getZip(), name );
	}
	
}
