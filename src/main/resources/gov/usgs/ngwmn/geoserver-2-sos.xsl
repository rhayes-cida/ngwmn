<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"

	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	
	xmlns:wfs="http://www.opengis.net/wfs" 
	xmlns:ngwmn="gov.usgs.cida.ngwmn"
	xmlns:gml-unversioned="http://www.opengis.net/gml" 
	
	xmlns:sos="http://www.opengis.net/sos/2.0" 
	xmlns:gml="http://www.opengis.net/gml/3.2"
	xmlns:swes="http://www.opengis.net/swes/2.0" 
	xmlns:om="http://www.opengis.net/om/2.0"
	xmlns:sf="http://www.opengis.net/sampling/2.0" 
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:sams="http://www.opengis.net/samplingSpatial/2.0" 
	xmlns:x="urn:gin:include:1.0"
		
	exclude-result-prefixes="wfs gml-unversioned ngwmn "
	
	
	xmlns:xalan="http://xml.apache.org/xalan"
	
	>
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" xalan:indent-amount="2"/>
	
	<xsl:template match="/wfs:FeatureCollection">
	<sos:GetFeatureOfInterestResponse>
		<xsl:apply-templates select=".//ngwmn:VW_GWDP_GEOSERVER"/>
	</sos:GetFeatureOfInterestResponse>
	</xsl:template>
	
	<xsl:template match="ngwmn:SITE_NAME">
		<gml:description><xsl:value-of select="text()"/></gml:description>
	</xsl:template>
	
	<xsl:template match="gml-unversioned:Point">
		<gml:Point>
			<gml:pos srsName="{@srsName}"><xsl:value-of select="translate(gml-unversioned:coordinates/text(),',',' ')"/></gml:pos>
		</gml:Point>
	</xsl:template>
	
	<xsl:template match="ngwmn:VW_GWDP_GEOSERVER">
	<sos:featureMember>
		<sams:SF_SpatialSamplingFeature gml:id="{translate(ngwmn:MY_SITEID/text(),':','.')}">
			<xsl:apply-templates select="ngwmn:SITE_NAME"/>
			<gml:identifier codeSpace="urn:ngwmn">/sites/<xsl:value-of select="ngwmn:MY_SITEID/text()"/></gml:identifier>
			<sf:type
				xlink:href="http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint"></sf:type>
			<sf:sampledFeature xlink:href="urn:ogc:def:nil:OGC:unknown"></sf:sampledFeature>
			<sams:shape>
				<xsl:apply-templates select="ngwmn:GEOM/gml-unversioned:Point"/>
			</sams:shape>
		</sams:SF_SpatialSamplingFeature>
	</sos:featureMember>
	</xsl:template>
	
	</xsl:stylesheet>