package gov.usgs.ngwmn.dm.io.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.transform.Transformer;

import org.joda.time.LocalDate;

public class DirectCSVOutputStreamWithDates extends DirectCSVOutputStream {
	private Date beginDate;
	private Date endDate;
	
	public DirectCSVOutputStreamWithDates(OutputStream out) throws IOException {
		super(out);
		setTransform("/gov/usgs/ngwmn/wl2csv-dates.xsl");
		logger.debug("created");
	}

	@Override
	protected void setupTransform(Transformer t) {
		super.setupTransform(t);
		
		if (getBeginDate() != null) {
			LocalDate dBegin = new LocalDate(getBeginDate());
			t.setParameter("beginDate", dBegin.toString());
		}
		if (getEndDate() != null) {
			LocalDate dEnd = new LocalDate(getEndDate());
			t.setParameter("endDate", dEnd.toString());			
		}
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DirectCSVOutputStreamWithDates [");
		builder.append("beginDate=");
		builder.append(beginDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", xformResourceName=");
		builder.append(xformResourceName);
		builder.append(", out=");
		builder.append(out);
		builder.append(", agency=");
		builder.append(agency);
		builder.append(", site=");
		builder.append(site);
		builder.append("]");
		return builder.toString();
	}

	
}
