package gov.usgs.ngwmn.dm.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableMap;

@ThreadSafe
public class PipeStatistics {
	
	@ThreadSafe
	public static enum Status {
		OPEN(false),
		STARTED(false),
		FAIL(true),
		DONE(true);
	
		private boolean done;
		private boolean isDone() {
			return done;
		}
		Status(boolean isDone) {
			done = isDone;
		}
		
		public String as4Char() {
			return name().substring(0, 4);
		}
		
		// invert map for 4-char representation
		private static Map<String,Status> invert4;
		static {
			Map<String,Status> tmp = new HashMap<String, PipeStatistics.Status>(values().length);
			for (Status s : values()) {
				Status prev = tmp.put(s.as4Char(), s);
				if (prev != null) {
					throw new RuntimeException("Conflict on 4char representation " + s.as4Char());
				}
			}	
			invert4 = ImmutableMap.copyOf(tmp);
		}
		
		public static Status by4Char(String c4) {
			return invert4.get(c4);
		}
	}

	private long count;
	private PipeStatistics.Status status = Status.OPEN;
	private long start = 0;
	private long end = 0;
	private Class<?> calledBy;
	private Specifier specifier;
	private String source;
	private String digest;
	
	public synchronized long getCount() {
		return count;
	}

	public synchronized void incrementCount(long c) {
		this.count += c;
	}

	public synchronized PipeStatistics.Status getStatus() {
		return status;
	}

	public synchronized void setStatus(PipeStatistics.Status status) {
		this.status = status;
		if (status.isDone()) {
			throw new RuntimeException("Use markEnd instead");
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PipeStatistics [count=").append(count)
				.append(", status=").append(status)
				.append(", start=").append(start)
				.append(", calledBy=").append(calledBy)
				.append(", specifier=").append(specifier)
				.append(", elapsedMSec=").append(getElapsedMSec())
				.append("]");
		return builder.toString();
	}
	
	public synchronized void markStart() {
		if (status == Status.STARTED) {
			// may have been pre-started, let it ride.
			return;
		}
		if (status != Status.OPEN) {
			throw new RuntimeException("Improper pre-start status");
		}
		status = Status.STARTED;
		start = System.currentTimeMillis();
	}
	
	public synchronized void markEnd(Status endStatus) {
		end = System.currentTimeMillis();
		if (status != Status.STARTED) {
			throw new RuntimeException("Improper pre-end status");
		}
		status = endStatus;
		this.notifyAll();
	}
	
	public synchronized Long getElapsedMSec() {
		if (start > 0 && end > 0) {
			return end-start;
		}
		return null;
	}

	public synchronized Class<?> getCalledBy() {
		return calledBy;
	}

	public synchronized void setCalledBy(Class<?> calledBy) {
		this.calledBy = calledBy;
	}

	public Double getElapsedTime() {
		Long el = getElapsedMSec();
		if (el == null) {
			return null;
		}
		return Double.valueOf(el / 1000.0);
	}
	
	public synchronized Date getStartDate() {
		if (start > 0) {
			return new Date(start);
		}
		return null;
	}

	public synchronized Date getEnd() {
		if (end > 0) {
			return new Date(end);
		}
		return null;
	}

	public boolean isDone() {
		return getStatus().done;
				
	}

	/** Beware that specifier may be shared betwen threads.
	 * 
	 * @return
	 */
	public synchronized Specifier getSpecifier() {
		return specifier;
	}

	public synchronized void setSpecifier(Specifier specifier) {
		this.specifier = specifier;
	}

	public synchronized String getSource() {
		return source;
	}

	public synchronized void setSource(String source) {
		this.source = source;
	}

	public synchronized String getDigest() {
		return digest;
	}

	public synchronized void setDigest(String digest) {
		this.digest = digest;
	}
	
	
}
