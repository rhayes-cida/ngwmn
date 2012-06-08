package gov.usgs.ngwmn.dm.io;

import java.util.LinkedHashMap;
import java.util.Map;

import gov.usgs.ngwmn.dm.spec.Specifier;

public class SpecifierEntry implements EntryDescription {

	protected Specifier spec;
	protected String ext;
	
	public SpecifierEntry(Specifier specifier) {
		spec   = specifier;
	}
	
	@Override
	public String entryName() {
		String suffix = baseName();
		
		String name = new StringBuilder()
		.append(spec.getAgencyID())
		.append('_')
		.append(spec.getFeatureID()) 
		.append('_')
		.append(suffix)
		.toString();
		
		return name;
	}
	@Override
	public String baseName() {
		String suffix = (ext==null) ? spec.getTypeID().suffix : ext;
		
		String name = new StringBuilder()
		.append(spec.getTypeID())
		.append('.')
		.append(suffix)
		.toString();
		
		return name;
	}

	@Override
	public void extension(String extension) {
		ext = extension;
	}

	@Override
	public Map<String, String> constColumns() {
		Map<String,String> data = new LinkedHashMap<String,String>();
		data.put("Agency", spec.getAgencyID());
		data.put("Site",   spec.getFeatureID());
		return data;
	}
}
