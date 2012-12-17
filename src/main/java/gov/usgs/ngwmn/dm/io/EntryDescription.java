package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.util.Map;

public interface EntryDescription {
	
	// return entry name for file or bundle naming
	// for ngwmn this is used for zip entry and file names
	String entryName();

	// return entry name for file or bundle naming
	// for ngwmn this is used for zip entry and file names
	String baseName();
	
	// allow the makeEntry (esp transformers) to change the file extension
	// this should override any default name extension
	// for nqwmn the data starts out as XML and become CSV or XLSX files
	void extension(String ext);

	// return a possible collection of default value constants
	// when joining data into one file, it is useful to add tagging data columns
	// for ngwmn the agency and site are not extracted from the files
	Map<String,String> constColumns();
	
	/** May return null, if this entry does not pertain to a speciic well.
	 * 
	 */
	Specifier getSpecifier();
}
