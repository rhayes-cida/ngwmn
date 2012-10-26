package gov.usgs.ngwmn.dm.prefetch;

import java.util.Date;

public class Observation {
	private int cacheId;
	private int month;
	private Date dt;
	private Double depth;
	
	public int getCacheId() {
		return cacheId;
	}
	public void setCacheId(int cacheId) {
		this.cacheId = cacheId;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public Date getDt() {
		return dt;
	}
	public void setDt(Date dt) {
		this.dt = dt;
	}
	public Double getDepth() {
		return depth;
	}
	public void setDepth(Double depth) {
		this.depth = depth;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Observation [cacheId=");
		builder.append(cacheId);
		builder.append(", month=");
		builder.append(month);
		builder.append(", dt=");
		builder.append(dt);
		builder.append(", depth=");
		builder.append(depth);
		builder.append("]");
		return builder.toString();
	}
	
	
}