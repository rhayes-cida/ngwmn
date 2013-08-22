<?xml version="1.0" encoding="UTF-8"?>
<!--  translate the ngwmn: namespaced output of our geoserver NGWMN layer to GWML -->
<xsl:stylesheet version="2.0" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"

	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	
	xmlns:wfs="http://www.opengis.net/wfs" 
	xmlns:ngwmn="gov.usgs.cida.ngwmn"
	xmlns:gml="http://www.opengis.net/gml" 
	xmlns:gwml="http://www.nrcan.gc.ca/xml/gwml/1"
	
	xmlns:gml32="http://www.opengis.net/gml/3.2"
	xmlns:om="http://www.opengis.net/om/2.0"
	xmlns:xlink="http://www.w3.org/1999/xlink"
		
	exclude-result-prefixes="ngwmn gml32 xalan om "
	
	
	xmlns:xalan="http://xml.apache.org/xalan"
	
	>
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" xalan:indent-amount="2"/>
	
	<xsl:template match="/wfs:FeatureCollection">
	<xsl:text >
</xsl:text>
	<wfs:FeatureCollection>
		<!-- metadata can go here -->
		<xsl:apply-templates select=".//gml:featureMember"/>
	</wfs:FeatureCollection>
	</xsl:template>
	
	<xsl:template match="gml:featureMember">
		<gml:featureMember>
			<xsl:apply-templates select="ngwmn:VW_GWDP_GEOSERVER"/>
		</gml:featureMember>
	</xsl:template>
	
	<xsl:template match="ngwmn:SITE_NAME">
		<gml:description><xsl:value-of select="text()"/></gml:description>
	</xsl:template>
	
	<xsl:template match="gml:Point">
		<gml:Point>
			<gml:pos srsName="{@srsName}"><xsl:value-of select="translate(gml:coordinates/text(),',',' ')"/></gml:pos>
		</gml:Point>
	</xsl:template>
	
	<xsl:template match="ngwmn:VW_GWDP_GEOSERVER">
		<gwml:WaterWell gml:id="{@fid}">
		<xsl:choose>
			<xsl:when test="ngwmn:SITE_NAME">
					<gml:description><xsl:value-of select="ngwmn:SITE_NAME"/></gml:description>
			</xsl:when>
			<xsl:otherwise>
					<gml:description><xsl:value-of select="ngwmn:MY_SITEID"/></gml:description>
			</xsl:otherwise>
		</xsl:choose>
		<!-- lots more stuff -->
		</gwml:WaterWell>
	</xsl:template>
	
	</xsl:stylesheet>