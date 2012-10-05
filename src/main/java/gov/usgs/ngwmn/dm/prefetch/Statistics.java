package gov.usgs.ngwmn.dm.prefetch;

import java.util.Date;

public class Statistics {
	int waterlevel_cache_id;
	double cum_distribution;
	double percent_rank;
	int count;
	int rank;
	Date min_date;
	Date max_date;
	double min_depth;
	double max_depth;
	
	
	public int getWaterlevel_cache_id() {
		return waterlevel_cache_id;
	}
	public void setWaterlevel_cache_id(int waterlevel_cache_id) {
		this.waterlevel_cache_id = waterlevel_cache_id;
	}
	public double getCum_distribution() {
		return cum_distribution;
	}
	public void setCum_distribution(double cum_distribution) {
		this.cum_distribution = cum_distribution;
	}
	public double getPercent_rank() {
		return percent_rank;
	}
	public void setPercent_rank(double percent_rank) {
		this.percent_rank = percent_rank;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public Date getMin_date() {
		return min_date;
	}
	public void setMin_date(Date min_date) {
		this.min_date = min_date;
	}
	public Date getMax_date() {
		return max_date;
	}
	public void setMax_date(Date max_date) {
		this.max_date = max_date;
	}
	public double getMin_depth() {
		return min_depth;
	}
	public void setMin_depth(double min_depth) {
		this.min_depth = min_depth;
	}
	public double getMax_depth() {
		return max_depth;
	}
	public void setMax_depth(double max_depth) {
		this.max_depth = max_depth;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Statistics [waterlevel_cache_id=");
		builder.append(waterlevel_cache_id);
		builder.append(", cum_distribution=");
		builder.append(cum_distribution);
		builder.append(", percent_rank=");
		builder.append(percent_rank);
		builder.append(", count=");
		builder.append(count);
		builder.append(", rank=");
		builder.append(rank);
		builder.append(", min_date=");
		builder.append(min_date);
		builder.append(", max_date=");
		builder.append(max_date);
		builder.append(", min_depth=");
		builder.append(min_depth);
		builder.append(", max_depth=");
		builder.append(max_depth);
		builder.append("]");
		return builder.toString();
	}
	
	
}