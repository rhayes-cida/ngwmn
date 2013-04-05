package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.WellDataType;

public class QualityQueryFlowFactory extends QueryFlowFactory {

	/* expected columns:
AgencyCd
SiteNo
Date
Time
TimeZone
CharacteristicName
Value
Unit
ResultStatus
ValueType
USGSPCode
SampleFraction
ResultComment
TemperatureBasis
DetectionCondition
MethodIdentifier
MethodContext
MethodName
MethodDescription
QuantitationLimitType
QuantitationLimitValue
QuantitationLimitUnit

	 */
	
/*
 * Parser mapping:
 
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

*/
	
	@Override
	String getQuery() {
		
		return "select\n" +
				"	qc.agency_cd 		AgencyCd, \n" +
				"	qc.site_no 		SiteNo,\n" +
				"	xq.* \n" +
				"	from \n" + 
				"		GW_DATA_PORTAL.QUALITY_CACHE	qc,\n" + 
				"		XMLTable(\n" + 
				"			'for $r in /*:Results/Result \n" + 
				"			 return $r\n" + 
				"			'\n" + 
				"			passing qc.xml\n" + 
				"			\n" + 
				"			columns \n" +
				"\"Date\" varchar(80) path '*:date',\n" + 
				"\"Time\" varchar(80) path '*:time',\n" + 
				"\"TimeZone\" varchar(80) path '*:zone',\n" + 
				"\"CharacteristicName\" varchar(80) path 'normalize-space(*:ResultDescription/*:CharacteristicName)',\n" + 
				"\"Value\" varchar(80) path 'normalize-space(*:ResultDescription/*:ResultMeasure/*:ResultMeasureValue)',\n" + 
				"\"Unit\" varchar(80) path 'normalize-space(*:ResultDescription/*:ResultMeasure/*:MeasureUnitCode)',\n" + 
				"\"ResultStatus\" varchar(80) path 'normalize-space(*:ResultDescription/*:ResultStatusIdentifier)',\n" + 
				"\"ValueType\" varchar(80) path 'normalize-space(*:ResultDescription/*:ResultValueTypeName)',\n" + 
				"\"USGSPCode\" varchar(80) path '*:ResultDescription/*:USGSPCode',\n" + 
				"\"SampleFraction\" varchar(80) path 'normalize-space(*:ResultDescription/*:ResultSampleFractionText)',\n" + 
				"\"ResultComment\" varchar(80) path 'normalize-space(*:ResultDescription/*:ResultCommentText)',\n" + 
				"\"TemperatureBasis\" varchar(80) path 'normalize-space(*:ResultDescription/*:ResultTemperatureBasisText)',\n" + 
				"\"DetectionCondition\" varchar(80) path 'normalize-space(*:ResultDescription/*:ResultDetectionConditionText)',\n" + 
				"\"MethodIdentifier\" varchar(80) path '*:ResultAnalyticalMethod/*:MethodIdentifier',\n" + 
				"\"MethodContext\" varchar(80) path 'normalize-space(*:ResultAnalyticalMethod/*:MethodIdentifierContext)',\n" + 
				"\"MethodName\" varchar(80) path '*:ResultAnalyticalMethod/*:MethodName',\n" + 
				"\"MethodDescription\" varchar(80) path 'normalize-space(*:ResultAnalyticalMethod/*:MethodDescriptionText)',\n" + 
				"\"QuantitationLimitType\" varchar(80) path 'normalize-space(*:ResultLabInformation/*:ResultDetectionQuantitationLimit/*:DetectionQuantitationLimitTypeName)',\n" + 
				"\"QuantitationLimitValue\" varchar(80) path '*:ResultLabInformation/*:ResultDetectionQuantitationLimit/*:DetectionQuantitationLimitMeasure/*:MeasureValue',\n" + 
				"\"QuantitationLimitUnit\" varchar(80) path '*:ResultLabInformation/*:ResultDetectionQuantitationLimit/*:DetectionQuantitationLimitMeasure/*:MeasureUnitCode'\n" + 
				"		) xq\n" + 
				"	WHERE QC.AGENCY_CD = ?\n" +
				"   AND QC.SITE_NO = ?\n" +
				"   AND QC.PUBLISHED = 'Y'\n" +
				"   AND xq.\"Date\" between coalesce(?,'1903-01-01') and coalesce(?,to_char(sysdate + 1,'YYYY-MM-DD'))\n" + 
				"" + 
				"   order by \n" + 
				"    xq.\"Date\" asc, \n" + 
				"    xq.\"Time\" asc,\n" + 
				"    xq.\"USGSPCode\" asc\n" + 
				"";
	}

	@Override
	void checkType(WellDataType wellDataType) {
		if (wellDataType != WellDataType.QUALITY) {
			throw new RuntimeException("require QUALITY");
		}
	}

	public static void main(String[] argv) {
		QualityQueryFlowFactory ego = new QualityQueryFlowFactory();
		System.out.print(ego.getQuery());
		System.out.println(";");
	}
}
