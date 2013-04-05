<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:wml2="http://www.wron.net.au/waterml2"
	xmlns:mediator="xalan://gov.usgs.ngwmn.WaterlevelMediator"
	xmlns:swe="http://www.opengis.net/swe/2.0" 
	xmlns:gwdp="https://github.com/USGS-CIDA/ngwmn/sos"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:wqx="http://qwwebservices.usgs.gov/schemas/WQX-Outbound/2_0/"
	>
	
	<xsl:output omit-xml-declaration="yes" indent="no" method="text" />
	<xsl:strip-space elements="*" />

	<xsl:template match="/">
		<xsl:param name="agency" select="'Agency'" />
		<xsl:param name="site" select="'Site'" />
		<xsl:param name="emit_header" select="false()"/>
		<xsl:param name="beginDate" />
		<xsl:param name="endDate" />

		<xsl:if test="$emit_header">
			<xsl:text>AgencyCd, SiteNo, Date, Time, TimeZone, CharacteristicName, Value, Unit, Result Status, Value Type, USGS PCode, Sample Fraction, Result Comment, Tempeature Basis, Detection Condition, Method Identifier, Method Context, Method Name, Method Description, Quantitation Limit Tyep, Quantitation Limit Value, Quantitation Limit Unit</xsl:text>
			<xsl:text>&#xa;</xsl:text>
		</xsl:if>
		
		<xsl:apply-templates select="Results/Result">
			<xsl:with-param name="agency" select="$agency" />
			<xsl:with-param name="site" select="$site" />
			<xsl:with-param name="beginDate" select="$beginDate"/>
			<xsl:with-param name="endDate" select="$endDate"/>
		</xsl:apply-templates>

	</xsl:template>

	<xsl:template match="Result">
		<xsl:param name="agency" />
		<xsl:param name="site" />
		<xsl:param name="beginDate"/>
		<xsl:param name="endDate"/>

		<xsl:if test="mediator:between($beginDate,string(./date),$endDate)">
		
			<xsl:value-of select="$agency" />
			<xsl:text>,</xsl:text>
			<xsl:value-of select="$site" />
			<xsl:text>,</xsl:text>
		
<xsl:value-of select="normalize-space(./date)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./time)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./zone)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:CharacteristicName)" />
<xsl:value-of select="normalize-space(./ResultDescription/CharacteristicName)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultMeasure/wqx:ResultMeasureValue)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultMeasure/ResultMeasureValue)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultMeasure/wqx:MeasureUnitCode)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultMeasure/MeasureUnitCode)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultStatusIdentifier)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultStatusIdentifier)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultValueTypeName)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultValueTypeName)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:USGSPCode)" />
<xsl:value-of select="normalize-space(./ResultDescription/USGSPCode)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultSampleFractionText)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultSampleFractionText)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultCommentText)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultCommentText)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultTemperatureBasisText)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultTemperatureBasisText)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultDetectionConditionText)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultDetectionConditionText)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultAnalyticalMethod/wqx:MethodIdentifier)" />
<xsl:value-of select="normalize-space(./ResultAnalyticalMethod/MethodIdentifier)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultAnalyticalMethod/wqx:MethodIdentifierContext)" />
<xsl:value-of select="normalize-space(./ResultAnalyticalMethod/MethodIdentifierContext)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultAnalyticalMethod/wqx:MethodName)" />
<xsl:value-of select="normalize-space(./ResultAnalyticalMethod/MethodName)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultAnalyticalMethod/wqx:MethodDescriptionText)" />
<xsl:value-of select="normalize-space(./ResultAnalyticalMethod/MethodDescriptionText)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultLabInformation/wqx:ResultDetectionQuantitationLimit/wqx:DetectionQuantitationLimitTypeName)" />
<xsl:value-of select="normalize-space(./ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitTypeName)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultLabInformation/wqx:ResultDetectionQuantitationLimit/wqx:DetectionQuantitationLimitMeasure/wqx:MeasureValue)" />
<xsl:value-of select="normalize-space(./ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitMeasure/MeasureValue)" />
<xsl:text>,</xsl:text>

<xsl:value-of select="normalize-space(./wqx:ResultLabInformation/wqx:ResultDetectionQuantitationLimit/wqx:DetectionQuantitationLimitMeasure/wqx:MeasureUnitCode)" />
<xsl:value-of select="normalize-space(./ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitMeasure/MeasureUnitCode)" />
<xsl:text>,</xsl:text>
		<xsl:text>&#xa;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="@*|node()">
		<!-- ignore -->
	</xsl:template>

</xsl:stylesheet>