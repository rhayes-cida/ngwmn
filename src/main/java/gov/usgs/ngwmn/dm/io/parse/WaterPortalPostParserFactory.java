package gov.usgs.ngwmn.dm.io.parse;

import static gov.usgs.ngwmn.WellDataType.LITHOLOGY;
import static gov.usgs.ngwmn.WellDataType.LOG;
import static gov.usgs.ngwmn.WellDataType.WATERLEVEL;
import gov.usgs.ngwmn.WellDataType;

import java.util.HashMap;
import java.util.Map;

public class WaterPortalPostParserFactory {

	public static final String LOG_EXCLUSION_COLUMNS_FULL_NAME[] = new String[] {
						"logElement/MappedInterval/observationMethod/CGI_TermValue/value/codeSpace",
						"logElement/MappedInterval/specification/HydrostratigraphicUnit/observationMethod/CGI_TermValue/value/codeSpace",
						"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/role",
						"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/role/codeSpace",
						"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/lithology/ControlledConcept/name/codeSpace",
						"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/proportion/CGI_TermValue/value",
						"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/proportion/CGI_TermValue/value/codeSpace",
						"logElement/MappedInterval/shape/LineString/srsDimension"};
	
	public static final String LOG_EXCLUSION_COLUMNS_DISPLAY_NAME[] = new String[] {
						"MappedInterval/observationMethod/CGI_TermValue/value/codeSpace",
						"HydrostratigraphicUnit/observationMethod/CGI_TermValue/value/codeSpace",
						"role",
						"role/codeSpace",
						"name/codeSpace",
						"proportion/CGI_TermValue/value",
						"proportion/CGI_TermValue/value/codeSpace",
						"srsDimension"};
	
	public static final String WATERLEVEL_EXCLUSION_COLUMNS_DISPLAY_NAME[] = new String[] {
						"uom"};

	public static final String NO_EXCLUSION_COLUMNS[] = new String[] {};

	public static final Map<WellDataType, String[]> exclusions;
	
	
	public static final Map<WellDataType, Map<String,String>> renameColumns;
	
	
	static {
		exclusions = new HashMap<WellDataType, String[]>();
		exclusions.put(LOG, LOG_EXCLUSION_COLUMNS_DISPLAY_NAME);
		exclusions.put(LITHOLOGY, LOG_EXCLUSION_COLUMNS_DISPLAY_NAME);
		exclusions.put(WATERLEVEL, WATERLEVEL_EXCLUSION_COLUMNS_DISPLAY_NAME);
		
		renameColumns = new HashMap<WellDataType, Map<String,String>>();
		
		HashMap<String, String> logRenames = new HashMap<String, String>();
		logRenames.put("MappedInterval/observationMethod/CGI_TermValue/value", "MappedIntervalValue");
		logRenames.put("HydrostratigraphicUnit/observationMethod/CGI_TermValue/value", "HydrostratigraphicUnitValue");
		renameColumns.put(LOG, logRenames);
		
		for (WellDataType type : WellDataType.values()) {
			if ( ! exclusions.containsKey(type) ) {
				exclusions.put(type, NO_EXCLUSION_COLUMNS);
			}
			if ( ! renameColumns.containsKey(type) ) {
				renameColumns.put(type, new HashMap<String, String>() );
			}
		}
	}
	
	public PostParser make(WellDataType type) {
		String removeCols[] = exclusions.get(type);
		Map<String, String> renameCols = renameColumns.get(type);
		
		WaterPortalPostParser postParser = new WaterPortalPostParser(removeCols, renameCols);
		return postParser;
	}
}
