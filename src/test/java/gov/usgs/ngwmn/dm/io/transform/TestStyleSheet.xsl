<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output omit-xml-declaration="yes" indent="no" method="text" />
	<xsl:strip-space elements="*" />

	<xsl:template match="/">
		<xsl:apply-templates select="//text"/>
	</xsl:template>
	
	<xsl:template match="text">
		<xsl:value-of select="."/>
		<xsl:text>&#xa;</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>