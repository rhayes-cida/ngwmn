package gov.usgs.ngwmn.dm.io;

import java.util.LinkedHashMap;
import java.util.Map;

import gov.usgs.ngwmn.dm.spec.Specifier;

public class SpecifierEntry implements EntryDescription {

	private Specifier spec;
	private String ext;
	
	public SpecifierEntry(Specifier specifier) {
		spec   = specifier;
	}
	
	@Override
	public String getName() {
		String suffix = (ext==null) ? spec.getTypeID().suffix : ext;
		
		String name = new StringBuilder()
		.append(spec.getAgencyID())
		.append('_')
		.append(spec.getFeatureID()) 
		.append('_')
		.append(spec.getTypeID())
		.append('.')
		.append(suffix)
		.toString();
		
		return name;
	}

	@Override
	public void setExtension(String extension) {
		ext = extension;
	}

	@Override
	public Map<String, String> getConstColumns() {
		Map<String,String> data = new LinkedHashMap<String,String>();
		data.put("agency", spec.getAgencyID());
		data.put("site",   spec.getFeatureID());
		return data;
	}
}
