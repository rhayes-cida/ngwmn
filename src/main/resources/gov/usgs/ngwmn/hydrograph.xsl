<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:wml2="http://www.wron.net.au/waterml2"
	xmlns:mediator="xalan://gov.usgs.ngwmn.WaterlevelMediator"
	xmlns:swe="http://www.opengis.net/swe/2.0" 
	xmlns:gwdp="https://github.com/USGS-CIDA/ngwmn/sos"
	>
	
	<xsl:output omit-xml-declaration="yes" indent="no" method="text" />
	<xsl:strip-space elements="*" />

	<xsl:template match="/">
		<xsl:param name="elevation">0.0</xsl:param>

		<xsl:text>Time, Mediated Value</xsl:text>
		<xsl:text>&#xa;</xsl:text>
		
		<xsl:apply-templates select="//wml2:TimeSeries//wml2:TimeValuePair">
			<xsl:with-param name="elevation" select="$elevation"/>
		</xsl:apply-templates>

	</xsl:template>

	<xsl:template match="wml2:TimeValuePair">
		<xsl:param name="elevation"/>

		<xsl:value-of select="mediator:csv_escape(string(.//wml2:time),',')" />
		<xsl:text>,</xsl:text>
		<xsl:text>-</xsl:text>
		<xsl:value-of select="mediator:mediate(string(.//swe:value),$elevation,string(.//gwdp:nwis/@direction))" />
		<xsl:text>&#xa;</xsl:text>
	</xsl:template>

	<xsl:template match="@*|node()">
		<!-- ignore -->
	</xsl:template>

</xsl:stylesheet>