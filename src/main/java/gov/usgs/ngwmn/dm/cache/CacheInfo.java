package gov.usgs.ngwmn.dm.cache;

import java.util.Date;

public class CacheInfo {
	private Date created;
	private boolean exists;
	private Date modified;
	private long length;
	private String md5;
	private String published;
	
	public CacheInfo(Date created, boolean exists, Date modified, long length, String hash, String published) {
		super();
		this.created = created;
		this.exists = exists;
		this.modified = modified;
		this.length = length;
		this.md5 = hash;
	}
	public Date getCreated() {
		return created;
	}
	public boolean isExists() {
		return exists;
	}
	public Date getModified() {
		return modified;
	}
	public long getLength() {
		return length;
	}
	public String getMd5() {
		return md5;
	}
	public Boolean getPublished() {
		if (published == null) {
			return null;
		}
		return ("Y".equalsIgnoreCase(published));
	}
	
}
