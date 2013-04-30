<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:wml2="http://www.wron.net.au/waterml2"
	xmlns:mediator="xalan://gov.usgs.ngwmn.WaterlevelMediator"
	xmlns:swe="http://www.opengis.net/swe/2.0" 
	xmlns:gwdp="https://github.com/USGS-CIDA/ngwmn/sos"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	
	exclude-result-prefixes="xsl wml2 mediator swe gwdp xs"
	>
	
	<xsl:output omit-xml-declaration="no" indent="yes" method="xml" />

	<xsl:template match="/">
		<xsl:param name="agency" select="'Agency'" />
		<xsl:param name="site" select="'Site'" />
		<xsl:param name="elevation">0.0</xsl:param>
		<xsl:param name="beginDate" />
		<xsl:param name="endDate" />

        <samples>
		<xsl:apply-templates select="//wml2:TimeSeries//wml2:TimeValuePair">
			<xsl:with-param name="agency" select="$agency" />
			<xsl:with-param name="site" select="$site" />
			<xsl:with-param name="elevation" select="$elevation"/>
			<xsl:with-param name="beginDate" select="$beginDate"/>
			<xsl:with-param name="endDate" select="$endDate"/>
		</xsl:apply-templates>
		</samples>
	</xsl:template>

	<xsl:template match="wml2:TimeValuePair">
		<xsl:param name="agency" />
		<xsl:param name="site" />
		<xsl:param name="elevation"/>
		<xsl:param name="beginDate"/>
		<xsl:param name="endDate"/>

		<xsl:if test="mediator:between($beginDate,string(./wml2:time),$endDate)">
		  <sample>
			<agency><xsl:value-of select="$agency" /></agency>
			<site><xsl:value-of select="$site" /></site>
			<time><xsl:value-of select="wml2:time" /></time>
			<pcode><xsl:value-of select="wml2:value/swe:Quantity/gwdp:nwis/@pcode" /></pcode>
			<direction><xsl:value-of select="wml2:value/swe:Quantity/gwdp:nwis/@direction" /></direction>
			<unit><xsl:value-of select="wml2:value/swe:Quantity/swe:uom/@code" /></unit>
			<value><xsl:value-of select="wml2:value/swe:Quantity/swe:value" /></value>
			<mediated-value><xsl:value-of select="mediator:mediate(string(wml2:value/swe:Quantity/swe:value),$elevation,string(wml2:value/swe:Quantity/gwdp:nwis/@direction))" /></mediated-value>
			<comment><xsl:value-of select="wml2:comment" /></comment>
		  </sample>
		</xsl:if>
	</xsl:template>

	<xsl:template match="@*|node()">
		<!-- ignore -->
	</xsl:template>

</xsl:stylesheet>