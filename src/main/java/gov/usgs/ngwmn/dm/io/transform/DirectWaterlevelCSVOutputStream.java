package gov.usgs.ngwmn.dm.io.transform;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Transformer;

public class DirectWaterlevelCSVOutputStream extends DirectCSVOutputStream {
	protected Double elevation;

	public DirectWaterlevelCSVOutputStream(OutputStream out) throws IOException {
		super("/gov/usgs/ngwmn/wl2csv-dates.xsl",out);
		logger.debug("created");
	}

	@Override
	protected void setupTransform(Transformer t) {
		super.setupTransform(t);
		
		if (getElevation() != null) {
			t.setParameter("elevation", String.valueOf(getElevation()));
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DirectWaterlevelCSVOutputStream [");
		builder.append("beginDate=");
		builder.append(beginDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", xformResourceName=");
		builder.append(xslHelper.getXformResourceName());
		builder.append(", out=");
		builder.append(out);
		builder.append(", agency=");
		builder.append(agency);
		builder.append(", site=");
		builder.append(site);
		builder.append("]");
		return builder.toString();
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	
}
