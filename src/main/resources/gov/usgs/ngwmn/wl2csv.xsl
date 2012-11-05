<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:wml2="http://www.wron.net.au/waterml2"
xmlns:swe="http://www.opengis.net/swe/2.0"
>
<xsl:output omit-xml-declaration="yes" indent="no" method="text" />
<xsl:strip-space elements="*"/> 

	<xsl:template match="/">AgencyCd, SiteNo, Time, Code, Value, Comment
<xsl:apply-templates select="//wml2:TimeSeries//wml2:TimeValuePair"></xsl:apply-templates>

	</xsl:template>
	
	<xsl:template match="wml2:TimeValuePair">
		<xsl:value-of select="'Agency'"/>,<xsl:value-of select="'Site'"/>,<xsl:value-of select=".//wml2:time"/>,<xsl:value-of select=".//swe:value"/><xsl:text>
</xsl:text>
	</xsl:template>

	<xsl:template match="@*|node()">
		<!--  ignore -->
	</xsl:template>

</xsl:stylesheet>