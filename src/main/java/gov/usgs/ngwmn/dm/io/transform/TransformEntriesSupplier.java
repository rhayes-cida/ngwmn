package gov.usgs.ngwmn.dm.io.transform;

import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.transform.TransformSupplier;
import gov.usgs.ngwmn.dm.spec.Encoding;

import java.io.OutputStream;

public class TransformEntriesSupplier extends TransformSupplier {
	
	public TransformEntriesSupplier(Supplier<OutputStream> output, Encoding encode) {
		super(output,encode,true);
	}

}
