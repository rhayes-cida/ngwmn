package gov.usgs.ngwmn.dm.prefetch;

public enum PrefetchOutcome {
	UNSTARTED,
	RUNNING,
	FINISHED,
	LIMIT_TIME,
	LIMIT_COUNT,
	INTERRUPTED,
	PROBLEM
}
