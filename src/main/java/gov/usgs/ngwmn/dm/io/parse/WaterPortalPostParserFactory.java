package gov.usgs.ngwmn.dm.io.parse;

import static gov.usgs.ngwmn.WellDataType.CONSTRUCTION;
import static gov.usgs.ngwmn.WellDataType.LITHOLOGY;
import static gov.usgs.ngwmn.WellDataType.LOG;
import static gov.usgs.ngwmn.WellDataType.QUALITY;
import static gov.usgs.ngwmn.WellDataType.WATERLEVEL;
import gov.usgs.ngwmn.WellDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public class WaterPortalPostParserFactory {
	private static final String ConstructionCoordinateFullName = "construction/Screen/screenElement/ScreenComponent/position/LineString/coordinates";
	private static final String LithologyCoordinateFullName    = "logElement/MappedInterval/shape/LineString/coordinates";
	
	private static final String ConstructionDepthPrefix = "Screen";
	private static final String LithologyDepthPrefix 	= "Lithology";
	
	private static final Map<WellDataType,Map<String,String>> coordinateMap = new HashMap<WellDataType, Map<String,String>>();

	public static final String[] LITHOLOGY_EXCLUSION_COLUMNS_FULL_NAME = new String[] {
						"logElement/MappedInterval/observationMethod/CGI_TermValue/value/codeSpace",
						"logElement/MappedInterval/specification/HydrostratigraphicUnit/observationMethod/CGI_TermValue/value/codeSpace",
						"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/role",
						"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/role/codeSpace",
						"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/lithology/ControlledConcept/name/codeSpace",
						"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/proportion/CGI_TermValue/value",
						"logElement/MappedInterval/specification/HydrostratigraphicUnit/composition/CompositionPart/proportion/CGI_TermValue/value/codeSpace",
						"logElement/MappedInterval/shape/LineString/srsDimension"};
	
	public static final String[] LITHOLOGY_EXCLUSION_COLUMNS_DISPLAY_NAME = new String[] {
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
	
	public static final String[] CONSTRUCTION_EXCLUSION_COLUMNS_DISPLAY_NAME = new String[] {
						"id",
						"srsName",
						"codeSpace"};
	public static final String[] LOG_EXCLUSION_COLUMNS_DISPLAY_NAME = Arrays.copyOf(
					ArrayUtils.addAll(LITHOLOGY_EXCLUSION_COLUMNS_DISPLAY_NAME,
									  CONSTRUCTION_EXCLUSION_COLUMNS_DISPLAY_NAME),
					LITHOLOGY_EXCLUSION_COLUMNS_DISPLAY_NAME.length + CONSTRUCTION_EXCLUSION_COLUMNS_DISPLAY_NAME.length,
					String[].class);
	
	public static final String[] WATERLEVEL_EXCLUSION_COLUMNS_DISPLAY_NAME = new String[] {
					"uom"};
	
	public static final String[] NO_EXCLUSION_COLUMNS = new String[] {};

	public static final Map<WellDataType, String[]> exclusions;
	
	
	public static final Map<WellDataType, Map<String,String>> renameColumns;
	
	
	static {
		exclusions = new HashMap<WellDataType, String[]>();
		exclusions.put(LOG,				LOG_EXCLUSION_COLUMNS_DISPLAY_NAME);
		exclusions.put(LITHOLOGY,		LITHOLOGY_EXCLUSION_COLUMNS_DISPLAY_NAME);
		exclusions.put(CONSTRUCTION,	CONSTRUCTION_EXCLUSION_COLUMNS_DISPLAY_NAME);
		exclusions.put(WATERLEVEL,		WATERLEVEL_EXCLUSION_COLUMNS_DISPLAY_NAME);
		
		renameColumns = new HashMap<WellDataType, Map<String,String>>();
		
		HashMap<String, String> lithologyRenames = new HashMap<String, String>();
		lithologyRenames.put("MappedInterval/observationMethod/CGI_TermValue/value", "ObservationMethod");
		lithologyRenames.put("id", "LithologyID");
		lithologyRenames.put("description", "LithologyDescription");
		lithologyRenames.put("ControlledConcept/name", "LithologyControlledConcept");
		renameColumns.put(LITHOLOGY, lithologyRenames);
		
		HashMap<String, String> constructionRenames = new HashMap<String, String>();
		constructionRenames.put("value", "ScreenMaterial");
		renameColumns.put(CONSTRUCTION, constructionRenames);
		
		HashMap<String, String> logRenames = new HashMap<String, String>();
		logRenames.putAll(lithologyRenames);
		logRenames.putAll(constructionRenames);
		renameColumns.put(LOG, logRenames);
		
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
		qualityRenames.put("ResultMeasureValue", "Value");
		qualityRenames.put("MeasureUnitCode", "Unit");
		qualityRenames.put("ResultStatusIdentifier", "ResultStatus");
		qualityRenames.put("ResultValueTypeName", "ValueType");
		qualityRenames.put("ResultTemperatureBasisText", "ResultTemperatureBasis");
		qualityRenames.put("ResultCommentText", "Comment");
		qualityRenames.put("MethodIdentifierContext", "MethodContext");
		qualityRenames.put("MethodDescriptionText", "MethodDescription");
		qualityRenames.put("ResultSampleFractionText", "SampleFraction");
		renameColumns.put(QUALITY, qualityRenames);
		
		for (WellDataType type : WellDataType.values()) {
			if ( ! exclusions.containsKey(type) ) {
				exclusions.put(type, NO_EXCLUSION_COLUMNS);
			}
			if ( ! renameColumns.containsKey(type) ) {
				renameColumns.put(type, new HashMap<String, String>() );
			}
		}
		
		Map<String,String> lithologyMap = new HashMap<String, String>();
		coordinateMap.put(LITHOLOGY, lithologyMap);
		Map<String,String> constructionMap = new HashMap<String, String>();
		coordinateMap.put(CONSTRUCTION, constructionMap);
		
		lithologyMap.put("fullName", LithologyCoordinateFullName);
		lithologyMap.put("prefix",   LithologyDepthPrefix);
		constructionMap.put("fullName", ConstructionCoordinateFullName);
		constructionMap.put("prefix",   ConstructionDepthPrefix);
	}
	
	public CompositePostParser make(WellDataType type) {
		String[] removeCols = exclusions.get(type);
		Map<String, String> renameCols = renameColumns.get(type);

		CompositePostParser postParser = new CompositePostParser();
		postParser.addPostParser( new ColumnRenamePostParser(renameCols) );
		postParser.addPostParser( new ColumnExclusionPostParser(removeCols) );
		
		// LITH and CON data types have coordinate cols
		if ( coordinateMap.containsKey(type) ) {
			postParser.addPostParser( makeCoordinatePostParser(type, removeCols, renameCols) );
		}
		
		if (type == LOG) { // LOG is a composite data type of LITH and CON
			for (WellDataType cType : coordinateMap.keySet()) {
				PostParser pp = makeCoordinatePostParser(cType, removeCols, renameCols);
				postParser.addPostParser(pp);
			}
		}

		return postParser;
	}

	protected PostParser makeCoordinatePostParser(WellDataType type,
			String[] removeCols, Map<String, String> renameCols) {
		
		Map<String, String> map = coordinateMap.get(type);
		return new CoordinatePostParser(map.get("fullName"), map.get("prefix"));
	}
}
