package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;


public class SupplyZipEntry extends Supplier<OutputStream> {

	private Specifier spec;
	private SupplyZipOutput parent;
	ZipEntryOutputStream zip;
	
	public SupplyZipEntry(SupplyZipOutput supplyZipOutput, Specifier specifier) {
		spec = specifier;
		parent    = supplyZipOutput;
	}

	@Override
	public ZipEntryOutputStream makeSupply(Specifier specifier) throws IOException {
		if (zip != null) return zip;
			
		String name = new StringBuilder()
					.append(spec.getAgencyID())
					.append('_')
					.append(spec.getFeatureID()) 
					.append('_')
					.append(spec.getTypeID())
					.append('.')
					.append(spec.getTypeID().suffix)
					.toString();
		
		return zip = new ZipEntryOutputStream( (ZipOutputStream) parent.begin(spec), name );
	}
	
}
