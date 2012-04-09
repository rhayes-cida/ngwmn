package gov.usgs.ngwmn.dm.harvest;

import gov.usgs.ngwmn.dm.DataFetcher;
import gov.usgs.ngwmn.dm.io.FakeInputInvoker;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;

public class FakeHarvestor implements DataFetcher {

	@Override
	public boolean configureInput(Specifier spec, Pipeline pipe)
			throws IOException {
		
		if (spec.getAgencyID().contains("FAIL")) {
			return false;
		}
		pipe.setInvoker(new FakeInputInvoker(spec));
		return true;
	}

}
