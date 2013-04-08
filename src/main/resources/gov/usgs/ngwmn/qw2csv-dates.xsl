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
		<xsl:param name="separator" select="','"/>

		<xsl:if test="$emit_header">
			<xsl:text>AgencyCd</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>SiteNo</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Date</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Time</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>TimeZone</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>CharacteristicName</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Value</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Unit</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Result Status</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Value Type</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>USGS PCode</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Sample Fraction</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Result Comment</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Temperature Basis</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Detection Condition</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Method Identifier</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Method Context</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Method Name</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Method Description</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Quantitation Limit Type</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Quantitation Limit Value</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Quantitation Limit Unit</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>&#xa;</xsl:text>
		</xsl:if>
		
		<xsl:apply-templates select="Results/Result">
			<xsl:with-param name="agency" select="$agency" />
			<xsl:with-param name="site" select="$site" />
			<xsl:with-param name="beginDate" select="$beginDate"/>
			<xsl:with-param name="endDate" select="$endDate"/>
			<xsl:with-param name="separator" select="$separator"/>
		</xsl:apply-templates>

	</xsl:template>

	<xsl:template match="Result">
		<xsl:param name="agency" />
		<xsl:param name="site" />
		<xsl:param name="beginDate"/>
		<xsl:param name="endDate"/>
		<xsl:param name="separator"/>

		<xsl:if test="mediator:between($beginDate,string(./date),$endDate)">
		
			<xsl:value-of select="$agency" />
			<xsl:value-of select="$separator"/>
			<xsl:value-of select="$site" />
			<xsl:value-of select="$separator"/>
		
<xsl:value-of select="normalize-space(./date)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./time)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./zone)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:CharacteristicName)" />
<xsl:value-of select="normalize-space(./ResultDescription/CharacteristicName)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultMeasure/wqx:ResultMeasureValue)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultMeasure/ResultMeasureValue)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultMeasure/wqx:MeasureUnitCode)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultMeasure/MeasureUnitCode)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultStatusIdentifier)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultStatusIdentifier)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultValueTypeName)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultValueTypeName)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:USGSPCode)" />
<xsl:value-of select="normalize-space(./ResultDescription/USGSPCode)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultSampleFractionText)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultSampleFractionText)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultCommentText)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultCommentText)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultTemperatureBasisText)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultTemperatureBasisText)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultDescription/wqx:ResultDetectionConditionText)" />
<xsl:value-of select="normalize-space(./ResultDescription/ResultDetectionConditionText)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultAnalyticalMethod/wqx:MethodIdentifier)" />
<xsl:value-of select="normalize-space(./ResultAnalyticalMethod/MethodIdentifier)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultAnalyticalMethod/wqx:MethodIdentifierContext)" />
<xsl:value-of select="normalize-space(./ResultAnalyticalMethod/MethodIdentifierContext)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultAnalyticalMethod/wqx:MethodName)" />
<xsl:value-of select="normalize-space(./ResultAnalyticalMethod/MethodName)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultAnalyticalMethod/wqx:MethodDescriptionText)" />
<xsl:value-of select="normalize-space(./ResultAnalyticalMethod/MethodDescriptionText)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultLabInformation/wqx:ResultDetectionQuantitationLimit/wqx:DetectionQuantitationLimitTypeName)" />
<xsl:value-of select="normalize-space(./ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitTypeName)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultLabInformation/wqx:ResultDetectionQuantitationLimit/wqx:DetectionQuantitationLimitMeasure/wqx:MeasureValue)" />
<xsl:value-of select="normalize-space(./ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitMeasure/MeasureValue)" />
<xsl:value-of select="$separator"/>

<xsl:value-of select="normalize-space(./wqx:ResultLabInformation/wqx:ResultDetectionQuantitationLimit/wqx:DetectionQuantitationLimitMeasure/wqx:MeasureUnitCode)" />
<xsl:value-of select="normalize-space(./ResultLabInformation/ResultDetectionQuantitationLimit/DetectionQuantitationLimitMeasure/MeasureUnitCode)" />
<xsl:value-of select="$separator"/>
		<xsl:text>&#xa;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="@*|node()">
		<!-- ignore -->
	</xsl:template>

</xsl:stylesheet>