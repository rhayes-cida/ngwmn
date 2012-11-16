package gov.usgs.ngwmn.dm.io.transform;

import java.io.OutputStream;

import javax.xml.transform.Transformer;

public class DirectCSVOutputStream extends XSLFilterOutputStream {

	protected boolean writtenHeaders = false;
	protected String agency;
	protected String site;
	protected Double elevation;
	
	public DirectCSVOutputStream(OutputStream out) throws Exception {
		super(out);
		setTransform("/gov/usgs/ngwmn/wl2csv.xsl");
	}

	/*
	 * 	<xsl:param name="agency" select="'Agency'" />
		<xsl:param name="site" select="'Site'" />
		<xsl:param name="elevation">0.0</xsl:param>
		<xsl:param name="emit_header" select="false()"/>
	 */
	
	
	@Override
	protected void setupTransform(Transformer t) {
		if (getAgency() != null) {
			t.setParameter("agency", getAgency());
		}
		if (getSite() != null) {
			t.setParameter("site",getSite());
		}
		if (getElevation() != null) {
			t.setParameter("elevation", String.valueOf(getElevation()));
		}
		if ( ! isWrittenHeaders()) {
			t.setParameter("emit_header", "true");
		}
		super.setupTransform(t);
	}

	public boolean isWrittenHeaders() {
		return writtenHeaders;
	}

	public void setWrittenHeaders(boolean writtenHeaders) {
		this.writtenHeaders = writtenHeaders;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	
}
