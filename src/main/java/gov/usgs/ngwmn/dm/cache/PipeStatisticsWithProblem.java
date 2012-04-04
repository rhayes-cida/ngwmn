package gov.usgs.ngwmn.dm.cache;

public class PipeStatisticsWithProblem {

	private PipeStatistics stats;
	private Throwable problem;
	
	public PipeStatisticsWithProblem(PipeStatistics stats, Throwable problem) {
		super();
		this.stats = stats;
		this.problem = problem;
	}

	public PipeStatistics getStats() {
		return stats;
	}

	public Throwable getProblem() {
		// Tell me, how do you feel about your mother?
		return problem;
	}

}
