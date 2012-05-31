package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

public class SpecifierEntry implements EntryName {

	private Specifier spec;
	private String ext;
	
	public SpecifierEntry(Specifier specifier) {
		spec   = specifier;
	}
	
	@Override
	public String name() {
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

}
