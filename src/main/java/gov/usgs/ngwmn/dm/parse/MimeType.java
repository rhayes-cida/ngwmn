package gov.usgs.ngwmn.dm.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO FormatType should really be renamed to MimeType
 * @author ilinkuo
 *
 */
public enum MimeType {

	//	EXCEL_FLAT("application/vnd.ms-excel", "xls"),
	//	DATA("text/plain", "txt"),
	//	SOAP1_1("text/xml", "xml"), 
	//	SOAP1_2("text/xml", "xml"),
	ZIP("application/zip", "zip"),

	// see http://www.w3.org/TR/xhtml-media-types/
	TEXT("text/plain", "txt"),

	// flattened types
	CSV("text/csv", "csv"),
	TAB("text/tab-separated-values", "tsv"),
	EXCEL("application/vnd.ms-excel", "xls"), 

	// markup types
	XHTML("application/xhtml+xml", "xhtml"), 
	HTML("text/html", "htm", "html"), 
	XML("text/xml", "xml"), // unspecified xml

	// transformed types
	RIS("text/x-research-info-systems", "ris"),//TODO This is the only one implemented
	ENDNOTE("text/x-endnote", "enl"),//TODO --sibley (falls through to RIS)
	BIBTEX("text/x-bibtex", "bib"),//TODO --sibley (falls through to RIS)

	//https://developers.google.com/kml/documentation/kml_tut#kml_server
	KML("application/vnd.google-earth.kml+xml", "kml"),
	KMZ("application/vnd.google-earth.kmz", "kmz"),
	
	JSON("text/javascript", "js"),

	// compression types
	FASTINFOSET("application/fastinfoset", "fi");

	private static final Map<String, MimeType> lookupMap = new HashMap<String, MimeType>();
	static {
		for (MimeType type: MimeType.values()) {
			lookupMap.put(type._mimeType.toUpperCase(), type);
			lookupMap.put(type.name().toUpperCase(), type);
			for (String suffix: type._fileSuffixes) {
				lookupMap.put(suffix.toUpperCase(), type);
			}
		}
	}

	// ======================
	// STATIC UTILITY METHODS
	// ======================
	/**
	 * Lookup my mimetype or suffix or name
	 * 
	 * @param mimeTypeOrSuffix
	 * @return
	 */
	public static MimeType lookup(String mimeTypeOrSuffix) {
		return lookupMap.get(mimeTypeOrSuffix.toUpperCase());
	}

	// ==================
	// INSTANCE VARIABLES
	// ==================
	private final String _mimeType;
	private final List<String> _fileSuffixes;
	private final String _fileSuffix;

	// ============
	// CONSTRUCTORS
	// ============
	private MimeType(String mimeType, String... fileSuffix) {
		// The first listed file suffix is the major one
		this._mimeType = mimeType;
		List<String> suffixes = new ArrayList<String>();
		for (String suffix: fileSuffix) {
			suffixes.add(suffix);
		}
		this._fileSuffixes = Collections.unmodifiableList(suffixes);
		this._fileSuffix = fileSuffix[0];
	}

	// ================
	// INSTANCE METHODS
	// ================
	public String getMimeType() {
		return this._mimeType;
	}
	public String getFileSuffix() {
		return this._fileSuffix;
	}
	public List<String> get_fileSuffixes() {
		return _fileSuffixes;
	}

	public boolean isXML() {
		return _mimeType.indexOf("xml") == _mimeType.length() - 3;
	}

	public String getSchemaType() {
		return (isXML())? _mimeType.substring(0, _mimeType.length() - 3): null;
	}


}
