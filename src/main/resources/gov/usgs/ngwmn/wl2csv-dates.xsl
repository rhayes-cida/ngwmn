<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:wml2="http://www.wron.net.au/waterml2"
	xmlns:mediator="xalan://gov.usgs.ngwmn.WaterlevelMediator"
	xmlns:swe="http://www.opengis.net/swe/2.0" 
	xmlns:gwdp="https://github.com/USGS-CIDA/ngwmn/sos"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	>
	
	<xsl:output omit-xml-declaration="yes" indent="no" method="text" />
	<xsl:strip-space elements="*" />

	<xsl:template match="/">
		<xsl:param name="agency" select="'Agency'" />
		<xsl:param name="site" select="'Site'" />
		<xsl:param name="elevation">0.0</xsl:param>
		<xsl:param name="emit_header" select="false()"/>
		<xsl:param name="beginDate" />
		<xsl:param name="endDate" />
		<xsl:param name="separator" select="','"/>

		<xsl:if test="$emit_header">
			<xsl:text>AgencyCd</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>SiteNo</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Time</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Original Parameter</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Original Direction</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Original Unit</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Original Value</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Depth to Water Below Land Surface in ft.</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>Observation Method</xsl:text><xsl:value-of select="$separator"/>
			<xsl:text>&#xa;</xsl:text>
		</xsl:if>
		
		<xsl:apply-templates select="//wml2:TimeSeries//wml2:TimeValuePair">
			<xsl:with-param name="agency" select="$agency" />
			<xsl:with-param name="site" select="$site" />
			<xsl:with-param name="elevation" select="$elevation"/>
			<xsl:with-param name="beginDate" select="$beginDate"/>
			<xsl:with-param name="endDate" select="$endDate"/>
			<xsl:with-param name="separator" select="$separator"/>
		</xsl:apply-templates>

	</xsl:template>

	<xsl:template match="wml2:TimeValuePair">
		<xsl:param name="agency" />
		<xsl:param name="site" />
		<xsl:param name="elevation"/>
		<xsl:param name="beginDate"/>
		<xsl:param name="endDate"/>
		<xsl:param name="separator"/>

		<xsl:if test="mediator:between($beginDate,string(./wml2:time),$endDate)">
			<xsl:value-of select="$agency" />
			<xsl:value-of select="$separator"/>
			
			<xsl:value-of select="$site" />
			<xsl:value-of select="$separator"/>
			
			<xsl:value-of select="mediator:csv_escape(string(.//wml2:time),string($separator))" />
			<xsl:value-of select="$separator"/>
			
			<xsl:value-of select=".//gwdp:nwis/@pcode" />
			<xsl:value-of select="$separator"/>
			
			<xsl:value-of select=".//gwdp:nwis/@direction" />
			<xsl:value-of select="$separator"/>
			
			<xsl:value-of select=".//swe:uom/@code" />
			<xsl:value-of select="$separator"/>
			
			<xsl:value-of select=".//swe:value" />
			<xsl:value-of select="$separator"/>
			
			<xsl:value-of select="mediator:mediate(string(.//swe:value),$elevation,string(.//gwdp:nwis/@direction))" />
			<xsl:value-of select="$separator"/>
			
			<xsl:value-of select="mediator:csv_escape(string(.//wml2:comment), string($separator))" />
			<xsl:text>&#xa;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="@*|node()">
		<!-- ignore -->
	</xsl:template>

</xsl:stylesheet>