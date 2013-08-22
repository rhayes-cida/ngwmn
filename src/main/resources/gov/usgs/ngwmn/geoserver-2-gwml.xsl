<?xml version="1.0" encoding="UTF-8"?>
<!--  translate the ngwmn: namespaced output of our geoserver NGWMN layer to GWML -->
<xsl:stylesheet version="2.0" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"

	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	
	xmlns:wfs="http://www.opengis.net/wfs" 
	xmlns:ngwmn="gov.usgs.cida.ngwmn"
	xmlns:gml="http://www.opengis.net/gml" 
	xmlns:gwml="http://www.nrcan.gc.ca/xml/gwml/1"
	xmlns:sa="http://www.opengis.net/sampling/1.0"
	xmlns:gsml="urn:cgi:xmlns:CGI:GeoSciML:2.0"
	
	xmlns:gml32="http://www.opengis.net/gml/3.2"
	xmlns:om="http://www.opengis.net/om/2.0"
	xmlns:xlink="http://www.w3.org/1999/xlink"
		
	exclude-result-prefixes="ngwmn gml32 xalan om "
	
	
	xmlns:xalan="http://xml.apache.org/xalan"
	
	>
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" xalan:indent-amount="2"/>
	
	<!-- Comments by RH: Roger Hayes [USGS], EB: Eric Boisvert [NRCAN] -->
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
	
	<xsl:template match="ngwmn:ALT_DATUM_CD[text() != 'NA']">
	 	<gml:metaDataProperty>
	 		<xsl:comment>Vertical datum</xsl:comment>
            <gml:EngineeringDatum gml:id="{generate-id()}">
               	<!-- EB: OPTIONAL: ALT_DATUM_CD -->
               	<gml:datumName><xsl:value-of select="."/></gml:datumName>
             </gml:EngineeringDatum>
      	</gml:metaDataProperty>
	
	</xsl:template>
	      <ngwmn:ALT_DATUM_CD>NGVD29</ngwmn:ALT_DATUM_CD>
	
	<xsl:template match="ngwmn:VW_GWDP_GEOSERVER">
		<gwml:WaterWell gml:id="{@fid}">
			<!-- EB: OPTIONAL: vertical datum -->
      	<xsl:apply-templates select="ngwmn:ALT_DATUM_CD"/>
		<xsl:choose>
			<xsl:when test="ngwmn:SITE_NAME">
					<gml:description><xsl:value-of select="ngwmn:SITE_NAME"/></gml:description>
			</xsl:when>
			<xsl:otherwise>
					<gml:description><xsl:value-of select="ngwmn:MY_SITEID"/></gml:description>
			</xsl:otherwise>
		</xsl:choose>

		<!--  as many names as you like, different codespaces -->
        <!-- EB: codeSpace = urn:ietf:rfc:2141 is a special codespace to say : this is the unique ID in context of this our organisation -->
        <gml:name codeSpace="urn:ietf:rfc:2141"><xsl:value-of select="ngwmn:ID"/></gml:name>

        <!-- EB: codeSpace = http://www.ietf.org/rfc/rfc2616 is a special codespace to say this is a linked data URL -->
        <xsl:comment>Data URL</xsl:comment>
        <gml:name codeSpace="http://www.ietf.org/rfc/rfc2616">http://cida.er.usgs.gov/ngwmn-geoserver/ngwmn/ows?service=WFS&amp;version=1.1.0&amp;request=GetFeature&amp;outputFormat=text/xml;%20subtype=gml/3.2&amp;FEATUREID=<xsl:value-of select="@fid"/></gml:name>

        <gml:name codeSpace="urn:usgs:cida:siteid"><xsl:value-of select="ngwmn:ID"/></gml:name>

        <!-- EB: MANDATORY: LOCAL_AQUIFER_CD and LOCAL_AQUIFER_NAME. This is what is being sampled is linked here : see O&M "featureOfInterest" in spec xlink:href should be a link to a representation of the feature of interest. 
                  If not possible, just stick an ID in there (211MRPAU) that would be "non resolvable" -->
        <!-- RH: Using national aquifer as local aquifer names are not centralized -->
        <xsl:comment>The URL for the national aquifer is purely conjecture.</xsl:comment>
        <sa:sampledFeature xlink:href="http://webservices.nationalatlas.gov/wms/water?SERVICE=WFS&amp;REQUEST=GetFeatureOfInterest?featureOfInterest={ngwmn:NAT_AQUIFER_CD}" xlink:title="{ngwmn:NAT_AQFR_DESC}"/>

        <!-- EB: geometry -->
        <sa:position>
			<xsl:copy-of select="ngwmn:GEOM/gml:Point"/>
      	</sa:position>

        <!-- EB: MANDATORY : ALT_VA and ALT_VA_UNITS , I suppose ALT_VA_UNITS = 1 means 'ft' -->
        <gwml:referenceElevation uom="urn:ogc:def:uom:UCUM:ft"><xsl:value-of select="ngwmn:ALT_VA"/></gwml:referenceElevation>

        <!-- EB: MANDATORY : WELL_DEPTH and WELL_DEPTH_UNITS , I suppose ALT_VA_UNIT = 1 means 'ft' -->
        <gwml:wellDepth>
        	<gsml:CGI_NumericValue>
            	<gsml:principalValue uom="urn:ogc:def:uom:UCUM:ft"><xsl:value-of select="ngwmn:WELL_DEPTH"/></gsml:principalValue>
         	</gsml:CGI_NumericValue>
       	</gwml:wellDepth>

        <!-- EB: OPTIONAL : WL_WELL_PURPOSE_DESC -->
		<!-- RH: TODO Decipher the WL and QW codes for CHARS TYPE PURPOSE-->
    	<gwml:wellPurpose codeSpace="urn.usgs:purposes"><xsl:value-of select="ngwmn:WL_WELL_PURPOSE_DESC"/></gwml:wellPurpose>

     	<!-- EB: MANDATORY : unknown in your case -->
    	<!-- RH: TODO What code space? Perhaps decode ngwmn:WL_DATA_FLAG -->
       	<gwml:wellStatus>
      		<gsml:CGI_TermValue>
           		<gsml:value codeSpace="urn:usgs:status">Unknown</gsml:value>
           	</gsml:CGI_TermValue>
     	</gwml:wellStatus>

      	<!-- EB: OPTIONAL : QW_WELL_TYPE_DESC -->
     	<gwml:wellType>
      		<gsml:CGI_TermValue>
       			<gsml:value codeSpace="urn:usgs:wellType"><xsl:value-of select="ngwmn:WL_WELL_TYPE_DESC"/></gsml:value>
      		</gsml:CGI_TermValue>
    	</gwml:wellType>

   		<!-- EB: MANDATORY : LINK and DATA_PROVIDER for xlink:title ( link to more info )  not sure about DATA_PROVIDER is the right label though-->
      	<!-- RH: TODO Should this be CIDA, or originating agency? -->
    	<gwml:onlineResource xlink:href="{ngwmn:LINK}" xlink:title="{ngwmn:AGENCY_NM}"/>

    	<!-- EB: MANDATORY: AGENCY_CD this is where you contact information are, a link to your page + some human readable string -->
       	<!-- RH: TODO Should this be CIDA, or originating agency? -->
      	<gwml:contact xlink:title="USGS CIDA" xlink:href="http://cida.usgs.gov"/>

		<xsl:comment>Well log will go here</xsl:comment>
		</gwml:WaterWell>
	</xsl:template>
	
	</xsl:stylesheet>