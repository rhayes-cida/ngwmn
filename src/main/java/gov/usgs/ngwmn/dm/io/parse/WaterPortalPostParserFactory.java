package gov.usgs.ngwmn.dm.io.parse;

import static gov.usgs.ngwmn.WellDataType.*;
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
						"HydrostratigraphicUnit/observationMethod/CGI_TermValue/value",
						"HydrostratigraphicUnit/purpose",
						"role",
						"role/codeSpace",
						"name/codeSpace",
						"proportion/CGI_TermValue/value",
						"proportion/CGI_TermValue/value/codeSpace",
						"UnconsolidatedMaterial/name",
						"UnconsolidatedMaterial/purpose",
						"UnconsolidatedMaterial/name",
						"UnconsolidatedMaterial/purpose",
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
		logRenames.put("MappedInterval/observationMethod/CGI_TermValue/value", "ObservationMethod");
		logRenames.put("id", "LithologyID");
		logRenames.put("description", "LithologyDescription");
		logRenames.put("ControlledConcept/name", "LithologyControlledConcept");
		renameColumns.put(LOG, logRenames);
		renameColumns.put(LITHOLOGY, logRenames);
		
		HashMap<String, String> levelRenames = new HashMap<String, String>();
		levelRenames.put("time", "DateTime");
		levelRenames.put("code", "Unit");
		levelRenames.put("value", "Value");
		levelRenames.put("comment", "ObservationMethod");
		renameColumns.put(WATERLEVEL, levelRenames);
		
		HashMap<String, String> qualityRenames = new HashMap<String, String>();
		qualityRenames.put("date", "Date");
		qualityRenames.put("time", "Time");
		qualityRenames.put("zone", "TimeZone");
		qualityRenames.put("ResultMeasureValue ", "Value");
		qualityRenames.put("MeasureUnitCode  ", "Unit");
		qualityRenames.put("ResultStatusIdentifier", "ResultStatus");
		qualityRenames.put("ResultValueTypeName", "ValueType");
		qualityRenames.put("ResultTemperatureBasisText", "ResultTemperatureBasis");
		qualityRenames.put("ResultCommentText", "Comment");
		qualityRenames.put("MethodIdentifierContext", "MethodContext");
		qualityRenames.put("MethodDescriptionText", "MethodDescription");
		renameColumns.put(QUALITY, qualityRenames);
		
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
		
		WaterPortalPostParser postParser;
		if (type == LOG || type == LITHOLOGY) {
			postParser = new LithologyPostParser(removeCols, renameCols);
		} else {
			postParser = new WaterPortalPostParser(removeCols, renameCols);
		}
		return postParser;
	}
}
