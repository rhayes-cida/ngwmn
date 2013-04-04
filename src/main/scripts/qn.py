col = [		("AgencyCd","AgencyCd"),
		("SiteNo","SiteNo"),
		("Result/date","Date"),
		("Result/time","Time"),
		("Result/zone","TimeZone"),
		#("Result/ResultDescription",null),
		("Result/ResultDescription/CharacteristicName","CharacteristicName"),
		#("Result/ResultDescription/ResultMeasure",null),
		("Result/ResultDescription/ResultMeasure/ResultMeasureValue","Value"),
		("Result/ResultDescription/ResultMeasure/MeasureUnitCode","Unit"),
		("Result/ResultDescription/ResultStatusIdentifier","ResultStatus"),
		("Result/ResultDescription/ResultValueTypeName","ValueType"),
		("Result/ResultDescription/USGSPCode","USGSPCode"),
		("Result/ResultDescription/ResultSampleFractionText","SampleFraction"),
		("Result/ResultDescription/ResultCommentText","ResultComment"),	
		("Result/ResultDescription/ResultTemperatureBasisText","TemperatureBasis"),
		("Result/ResultDescription/ResultDetectionConditionText","DetectionCondition"),
		#("Result/ResultAnalyticalMethod",null),
		("Result/ResultAnalyticalMethod/MethodIdentifier","MethodIdentifier"),
		("Result/ResultAnalyticalMethod/MethodIdentifierContext","MethodContext"),
		("Result/ResultAnalyticalMethod/MethodName","MethodName"),
		("Result/ResultAnalyticalMethod/MethodDescriptionText","MethodDescription"),
		#("Result/ResultLabInformation",null),
		#("Result/ResultLabInformation/ResultDetectionQuantitationLimit",null),
		("Result/ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitTypeName","QuantitationLimitType"),
		#("Result/ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitMeasure",null),
		("Result/ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitMeasure/MeasureValue","QuantitationLimitValue"),
		("Result/ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitMeasure/MeasureUnitCode","QuantitationLimitUnit")
]

fmt = "\"%s\" varchar(80) path '%s',"
for c in col:
	path = c[0].replace("/","/*:")
	path = path.replace("Result/","")
	print fmt % (c[1],path)

