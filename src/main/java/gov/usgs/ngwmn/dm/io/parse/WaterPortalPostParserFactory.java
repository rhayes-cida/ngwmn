package gov.usgs.ngwmn.dm.io.parse;

import static gov.usgs.ngwmn.WellDataType.CONSTRUCTION;
import static gov.usgs.ngwmn.WellDataType.LITHOLOGY;
import static gov.usgs.ngwmn.WellDataType.LOG;
import static gov.usgs.ngwmn.WellDataType.QUALITY;
import static gov.usgs.ngwmn.WellDataType.WATERLEVEL;
import static gov.usgs.ngwmn.WellDataType.REGISTRY;
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
	
	public static final String[] REGISTRY_EXCLUSION_COLUMNS_DISPLAY_NAME = new String[] {
		// These should, ideally, not appear in the input data now that we are using an explicit column list.
		// No harm and some benefit in leaving the exclusion list in place, though.
		"agency_cd",
		"site_no",
		"my_siteid",
		"agency_med",
		"display_flag",
		// "nat_aqfr_desc",
		"wl_data_flag",
		"qw_data_flag",
		"log_data_flag",
		"geom",	
		"geom_3785",	
		"insert_date",	
		"update_date",	
		"data_provider",	
		"wl_data_provider",	
		"qw_data_provider",	
		"lith_data_provider",	
		"const_data_provider",
		"unused"
		// ,"link"
	};
	
	public static final String[][] qualityMapping = new String[][] {
		{"AgencyCd","AgencyCd"},
		{"SiteNo","SiteNo"},
		{"Result/date","Date"},
		{"Result/time","Time"},
		{"Result/zone","TimeZone"},
		{"Result/ResultDescription",null},
		{"Result/ResultDescription/CharacteristicName","CharacteristicName"},
		{"Result/ResultDescription/ResultMeasure",null},
		{"Result/ResultDescription/ResultMeasure/ResultMeasureValue","Value"},
		{"Result/ResultDescription/ResultMeasure/MeasureUnitCode","Unit"},
		{"Result/ResultDescription/ResultStatusIdentifier","ResultStatus"},
		{"Result/ResultDescription/ResultValueTypeName","ValueType"},
		{"Result/ResultDescription/USGSPCode","USGSPCode"},
		{"Result/ResultDescription/ResultSampleFractionText","SampleFraction"},
		{"Result/ResultDescription/ResultCommentText","ResultComment"},	
		{"Result/ResultDescription/ResultTemperatureBasisText","TemperatureBasis"},
		{"Result/ResultDescription/ResultDetectionConditionText","DetectionCondition"},
		{"Result/ResultAnalyticalMethod",null},
		{"Result/ResultAnalyticalMethod/MethodIdentifier","MethodIdentifier"},
		{"Result/ResultAnalyticalMethod/MethodIdentifierContext","MethodContext"},
		{"Result/ResultAnalyticalMethod/MethodName","MethodName"},
		{"Result/ResultAnalyticalMethod/MethodDescriptionText","MethodDescription"},
		{"Result/ResultLabInformation",null},
		{"Result/ResultLabInformation/ResultDetectionQuantitationLimit",null},
		{"Result/ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitTypeName","QuantitationLimitType"},
		{"Result/ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitMeasure",null},
		{"Result/ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitMeasure/MeasureValue","QuantitationLimitValue"},
		{"Result/ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitMeasure/MeasureUnitCode","QuantitationLimitUnit"}
	};
	

	
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
		exclusions.put(REGISTRY, REGISTRY_EXCLUSION_COLUMNS_DISPLAY_NAME);
		
		
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

		if (type == QUALITY) {
			postParser.addPostParser(makeQualityPP());
		}
		return postParser;
	}

	
	protected PostParser makeQualityPP() {
		return new FixedOrderPostParser(qualityMapping);
	}


	protected PostParser makeCoordinatePostParser(WellDataType type,
			String[] removeCols, Map<String, String> renameCols) {
		
		Map<String, String> map = coordinateMap.get(type);
		return new CoordinatePostParser(map.get("fullName"), map.get("prefix"));
	}
}
