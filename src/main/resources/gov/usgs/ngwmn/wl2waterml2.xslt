<?xml version="1.0" encoding="UTF-8"?>
<!-- 
Transform the existing Cocoon waterlevel output to validating WaterML 2.0.
 -->
<xsl:stylesheet version="2.0" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"

	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	
	xmlns:wml2="http://www.opengis.net/waterml/2.0"
	xmlns:gml="http://www.opengis.net/gml/3.2"
	xmlns:om="http://www.opengis.net/om/2.0"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:gco="http://www.isotc211.org/2005/gco"
	xmlns:gmd="http://www.isotc211.org/2005/gmd"
	xmlns:sams="http://www.opengis.net/samplingSpatial/2.0"
	xmlns:sf="http://www.opengis.net/sampling/2.0"
	xmlns:swe="http://www.opengis.net/swe/2.0" 
	xmlns:sos="http://www.opengis.net/sos/2.0"
	
	xmlns:wml2-prelim="http://www.wron.net.au/waterml2"
	
	xmlns:h="http://apache.org/cocoon/request/2.0" 
	xmlns:fn="http://www.w3.org/2005/xpath-functions" 
	xmlns:wfs="http://www.opengis.net/wfs" 
	xmlns:gwml="http://www.nrcan.gc.ca/xml/gwml/1" 
	xmlns:functx="http://www.functx.com"
	xmlns:gml-unversioned="http://www.opengis.net/gml"
	xmlns:md="http://www.isotc211.org/2005/gmd"
	xmlns:gwdp="https://github.com/USGS-CIDA/ngwmn/sos"
	
	xmlns:xalan="http://xml.apache.org/xalan"
	
	exclude-result-prefixes="wml2-prelim h fn wfs gwml functx gml-unversioned gwdp "
	
	xsi:schemaLocation="
	http://www.opengis.net/waterml/2.0 http://schemas.opengis.net/waterml/2.0/waterml2.xsd
	http://www.opengis.net/gml/3.2 http://schemas.opengis.net/gml/3.2.1/gml.xsd
	http://www.opengis.net/om/2.0 http://schemas.opengis.net/om/2.0/observation.xsd
	http://www.w3.org/1999/xlink http://www.w3.org/1999/xlink.xsd
	http://www.isotc211.org/2005/gco http://www.isotc211.org/2005/gco/gco.xsd
	http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd
	http://www.opengis.net/sos/2.0 http://schemas.opengis.net/sos/2.0/sos.xsd
	http://www.opengis.net/swes/2.0 http://schemas.opengis.net/swes/2.0/swes.xsd
" 
	
	xmlns:date="http://exslt.org/dates-and-times"
	extension-element-prefixes="date"
	>
	
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" xalan:indent-amount="2"/>
	 
	<xsl:key name="TimeValuePair" match="wml2-prelim:TimeValuePair" use=".//gwdp:nwis/@pcode"/>
	
	<xsl:template match="om:ObservationCollection">
	<sos:GetObservationResponse
		
			xsi:schemaLocation="
	http://www.opengis.net/waterml/2.0 http://schemas.opengis.net/waterml/2.0/waterml2.xsd
	http://www.opengis.net/gml/3.2 http://schemas.opengis.net/gml/3.2.1/gml.xsd
	http://www.opengis.net/om/2.0 http://schemas.opengis.net/om/2.0/observation.xsd
	http://www.w3.org/1999/xlink http://www.w3.org/1999/xlink.xsd
	http://www.isotc211.org/2005/gco http://www.isotc211.org/2005/gco/gco.xsd
	http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd
	http://www.opengis.net/sos/2.0 http://schemas.opengis.net/sos/2.0/sos.xsd
	http://www.opengis.net/swes/2.0 http://schemas.opengis.net/swes/2.0/swes.xsd
	" 
	>
<!-- some optional stuff here -->
     
	<extension xmlns="http://www.opengis.net/swes/2.0">
		<gml:identifier codeSpace="urn:x-usgs:us.usgs.nwis"><xsl:value-of select="(//@gml-unversioned:id)[1]"/></gml:identifier>
		<gml:name ><xsl:attribute name="codeSpace" select="./gml-unversioned:name[0]/@codeSpace"/><xsl:value-of select="./gml-unversioned:name[0]/text()"/></gml:name>
		
		<wml2:metadata>
			<wml2:DocumentMetadata gml:id="{generate-id()}">
				<gml:metaDataProperty xlink:href="contact">
					<gml:GenericMetaData>http://cida.usgs.gov</gml:GenericMetaData>
				</gml:metaDataProperty>
				<wml2:generationDate><xsl:value-of select="date:date-time()"/></wml2:generationDate>
				<wml2:version xlink:href="http://www.opengis.net/waterml/2.0" xlink:title="WaterML 2.0"/>
		</wml2:DocumentMetadata>
		</wml2:metadata>
			<wml2:temporalExtent> <!--  (Same as USGS2sos.xlst for 1/om:metadata/wml2:ObservationMetadata/wml2:intendedSamplingInterval) -->
				<gml:TimePeriod gml:id="{generate-id((.//wml2-prelim:intendedSamplingInterval)[1])}"> <!-- See USGS2sos.xlst for $samplingIntervalId -->
					<gml:beginPosition><xsl:value-of select=".//wml2-prelim:intendedSamplingInterval//gml-unversioned:beginPosition"/></gml:beginPosition>
					<gml:endPosition><xsl:value-of select=".//wml2-prelim:intendedSamplingInterval//gml-unversioned:endPosition"/></gml:endPosition>
				</gml:TimePeriod>
			</wml2:temporalExtent>
	</extension>
	
	<sos:observationData>
		
	<!--<wml2:observationMember>-->
		<om:OM_Observation gml:id="{generate-id((.//wml2-prelim:WaterMonitoringObservation)[1])}" >
			<om:metadata>
				<wml2:ObservationMetadata>
					<gmd:contact>
						<gmd:CI_ResponsibleParty>
							<gmd:organisationName>
								<gco:CharacterString><xsl:value-of select="normalize-space((.//gmd:CI_ResponsibleParty/gmd:organisationName/*)[1])"/></gco:CharacterString>
							</gmd:organisationName>
							<gmd:role>
								<gmd:CI_RoleCode codeList="http://www.isotc211.org/2005/resources/CodeList/gmxCodelists.xml#CI_RoleCode" codeListValue="owner"/>
							</gmd:role>
						</gmd:CI_ResponsibleParty>
					</gmd:contact>
					<gmd:dateStamp>
						<gco:DateTime><xsl:value-of select="date:date-time()"/></gco:DateTime>
					</gmd:dateStamp>
					<gmd:identificationInfo/>
				</wml2:ObservationMetadata>
			</om:metadata>
			
			<xsl:comment>Phenomenon Time</xsl:comment>
			<om:phenomenonTime>
			<!--<xsl:apply-templates select=".//om:phenomenonTime" exclude-result-prefixes="gml wml2"/>-->
			</om:phenomenonTime>
						
			<xsl:comment>Result Time</xsl:comment>	
			<om:resultTime>
				<!--
				<gml:TimeInstant gml:id="{generate-id((.//om:resultTime/gml-unversioned:TimePeriod/gml-unversioned:beginPosition)[1])}">
					<gml:timePosition><xsl:value-of select=".//om:resultTime/gml-unversioned:TimePeriod/gml-unversioned:beginPosition"/></gml:timePosition>
				</gml:TimeInstant>
				-->
			</om:resultTime>
			
			<xsl:comment>Procedure</xsl:comment>	
<!-- This produces output with ugly namespaces. Sigh.			
   <xsl:copy-of select=".//om:procedure" copy-namespaces="no" exclude-result-prefixes="#all"/>
-->
			<om:procedure xlink:href="{.//om:procedure/@xlink:href}" />
			
			<om:observedProperty xlink:href="urn:ogc:def:property:OGC:GroundWaterLevel"/>
			
			<om:featureOfInterest xlink:title="{.//om:featureOfInterest/@xlink:title}" xlink:href="{encode-for-uri(.//om:featureOfInterest/@xlink:href)}">
				<!--
				<wml2:MonitoringPoint gml:id="{generate-id((.//om:parameter//gml-unversioned:Point)[1])}">
				 	<sf:sampledFeature xlink:href="{.//gmd:identificationInfo/@xlink:href}"/>
				
					<sams:shape>
						<xsl:apply-templates select=".//om:parameter//gml-unversioned:Point"/>
					</sams:shape>
				</wml2:MonitoringPoint>
				-->
			</om:featureOfInterest>
			
			<om:result>
			
			<xsl:variable name="TimePeriod" select=".//wml2-prelim:intendedSamplingInterval/gml-unversioned:TimePeriod"/>
			
			<!--   One MeasurementTimeseries for each distinct pcode -->
			<!-- Muenchian Method, see http://www.jenitennison.com/xslt/grouping/muenchian.html  -->
				
				<xsl:choose>
				<xsl:when test=".//gwdp:nwis/@pcode">
					<xsl:for-each select=".//wml2-prelim:TimeValuePair[count(.|key('TimeValuePair',.//gwdp:nwis/@pcode)[1])=1]">
					<xsl:sort select="wml2-prelim:time"/>
				
				<wml2:MeasurementTimeseries gml:id="pcode.{.//gwdp:nwis/@pcode}" >
					<wml2:metadata>
						<wml2:TimeseriesMetadata>
							<wml2:temporalExtent>
								<gml:TimePeriod gml:id="{generate-id()}">
									<gml:beginPosition><xsl:value-of select="(key('TimeValuePair',.//gwdp:nwis/@pcode)/wml2-prelim:time)[1]"/></gml:beginPosition>
									<gml:endPosition><xsl:value-of select="(key('TimeValuePair',.//gwdp:nwis/@pcode)/wml2-prelim:time)[last()]"/></gml:endPosition>
								</gml:TimePeriod>
							</wml2:temporalExtent>				
						</wml2:TimeseriesMetadata>
					</wml2:metadata>
					<wml2:defaultPointMetadata>
					 	<wml2:DefaultTVPMeasurementMetadata>
					 		<wml2:uom code="foot" xlink:href="http://www.opengis.net/def/uom/UCUM/0/foot" xlink:title="feet below land surface"/>
							<!--<xsl:apply-templates select=".//gwdp:nwis"/>-->
							<wml2:interpolationType xlink:href="http://www.opengis.net/def/waterml/2.0/interpolationType/Continuous" xlink:title="Continuous"/>
						</wml2:DefaultTVPMeasurementMetadata>
						
					</wml2:defaultPointMetadata>
		
					<xsl:for-each select="key('TimeValuePair',.//gwdp:nwis/@pcode)">
						<xsl:sort select="wml2-prelim:time"/>
						<xsl:apply-templates select="."/>
					</xsl:for-each>
				</wml2:MeasurementTimeseries>
				</xsl:for-each>
				</xsl:when>
				
				<xsl:otherwise>
				<xsl:apply-templates select=".//wml2-prelim:TimeSeries"/>
				</xsl:otherwise>
				</xsl:choose>
		
		
		</om:result>
		</om:OM_Observation>
		<!--</wml2:observationMember>-->
		
		
		<!--</wml2:Collection>-->
		       </sos:observationData>
		</sos:GetObservationResponse>
	</xsl:template>
	
	
	<!-- [not (.//gwdp:nwis/@pcode)] -->
	<xsl:template match="wml2-prelim:TimeSeries[not (.//gwdp:nwis/@pcode)]">
				<!--  non-pcode branch -->
				
				<wml2:MeasurementTimeseries gml:id="pcode.none" >
					<wml2:metadata>
						<wml2:TimeseriesMetadata>
							<wml2:temporalExtent>
								<!--
								<gml:TimePeriod gml:id="{generate-id()}">
									<gml:beginPosition><xsl:value-of select="wml2-prelim:domainExtent/gml-unversioned:TimePeriod/gml-unversioned:beginPosition"/></gml:beginPosition>
									<gml:endPosition><xsl:value-of select="wml2-prelim:domainExtent/gml-unversioned:TimePeriod/gml-unversioned:beginPosition"/></gml:endPosition>
								</gml:TimePeriod>
								-->
							</wml2:temporalExtent>				
						</wml2:TimeseriesMetadata>
					</wml2:metadata>
					<wml2:defaultPointMetadata>
					
						<wml2:DefaultTVPMeasurementMetadata>
							<wml2:uom code="foot" xlink:href="http://www.opengis.net/def/uom/UCUM/0/foot" xlink:title="feet below land surface"/>
							<!--<xsl:apply-templates select=".//gwdp:nwis"/>-->
							<wml2:interpolationType xlink:href="http://www.opengis.net/def/waterml/2.0/interpolationType/Continuous" xlink:title="Continuous"/>
						</wml2:DefaultTVPMeasurementMetadata>
						
					</wml2:defaultPointMetadata>
						<!--<wml2:uom code="foot" xlink:href="http://www.opengis.net/def/uom/UCUM/0/foot" xlink:title="feet below land surface"/>-->
						<xsl:apply-templates select=".//wml2-prelim:TimeValuePair"/>
				</wml2:MeasurementTimeseries>
	
	</xsl:template>
	
<!-- Replace gml-unversioned namespace with gml -->
  <xsl:template match='gml-unversioned:*'>
    <xsl:element name='gml:{local-name()}' >
      <xsl:apply-templates select='@*' />
      <xsl:apply-templates select='node()' />
    </xsl:element>
  </xsl:template>
  
<!-- Replace gml-unversioned namespace with gml in attributes -->
  <xsl:template match='@gml-unversioned:*'>
    <xsl:attribute name='gml:{local-name()}' >
    	<xsl:value-of select="."  />
    </xsl:attribute>
  </xsl:template>
  
<!-- Replace wml2-prelim namespace with wml2. -->
  <xsl:template match='wml2-prelim:*'>
    <xsl:element name='wml2:{local-name()}' >
      <xsl:apply-templates select='@*' />
      <xsl:apply-templates select='node()' />
    </xsl:element>
  </xsl:template>
  
<!-- Replace wml2-prelim namespace with wml2 in attributes -->
  <xsl:template match='@wml2-prelim:*'>
    <xsl:attribute name='wml2:{local-name()}' >
    	<xsl:value-of select="."  />
    </xsl:attribute>
  </xsl:template>

	<xsl:template match="wml2-prelim:TimeValuePair">
		<wml2:point>
						<wml2:MeasurementTVP>
							<wml2:time><xsl:value-of select="wml2-prelim:time"/></wml2:time>
							<wml2:value uom="{wml2-prelim:value/swe:Quantity/swe:uom/@code}"><xsl:value-of select="wml2-prelim:value/swe:Quantity/swe:value"/></wml2:value>
							<xsl:apply-templates mode="tvp" select="wml2-prelim:comment"/>
						</wml2:MeasurementTVP>
					</wml2:point>
	</xsl:template>
	  
	 <xsl:template match="wml2-prelim:comment" mode="tvp">
	 	<wml2:metadata><wml2:TVPMeasurementMetadata><wml2:comment><xsl:value-of select="."/></wml2:comment></wml2:TVPMeasurementMetadata></wml2:metadata>
	 </xsl:template>
	 
	<xsl:template match="om:parameter">
		<wml2:parameter>
			<xsl:apply-templates select="./om:NamedValue"/>
		</wml2:parameter>
	</xsl:template>
	
	<xsl:template match="gml-unversioned:pos">
		<gml:pos>
		<xsl:value-of select="translate(text(),',',' ')"/>
		</gml:pos>
	</xsl:template>
	
	<xsl:template match="gml-unversioned:Point">
	 	<gml:Point  srsName="{@srsName}" gml:id="{@gml-unversioned:id}">
			<xsl:apply-templates select="gml-unversioned:pos"/>
		</gml:Point>
	</xsl:template>
	
	<xsl:template match="gwdp:nwis">
		<wml2:qualifier>
			<swe:Category>
				<swe:identifier><xsl:value-of select="@pcode"/></swe:identifier>
				<swe:label><xsl:value-of select="@direction"/></swe:label>
				<swe:codeSpace xlink:href="http://nwis.waterdata.usgs.gov/usa/nwis/pmcodes" />
			</swe:Category>
		</wml2:qualifier>
	</xsl:template>
	
</xsl:stylesheet>
