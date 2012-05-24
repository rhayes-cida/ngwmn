package gov.usgs.ngwmn.dm.parse;

class ErrorChecker {
	int ehCount = 0, thCount = 0, evCount = 0, tvCount=0;
	private boolean throwExceptions;

	public ErrorChecker(boolean throwExceptions) {
		this.throwExceptions = throwExceptions;
	}
	
	// update methods
	public void updateElderHeaderCount(int size) {
		ehCount = Math.max(ehCount, size);			
	}
	public void updateTargetHeaderCount(int size) {
		thCount = Math.max(thCount, size);		
	}
	public void updateElderValueCount(int size) {
		evCount = Math.max(evCount, size);		
	}
	public void updateTargetValueCount(int size) {
		tvCount = Math.max(tvCount, size);		
	}
	
	// checks
	public boolean hasTooManyElderValuesError() {
		if (throwExceptions && evCount > ehCount) {
			throw new RuntimeException("Too many elder values -- headers: " + ehCount + " < values: " + evCount);
		}
		return evCount > ehCount;
	}
	public boolean hasTooManyTargetValuesError() {
		if (throwExceptions && tvCount > thCount) {
			throw new RuntimeException("Too many elder values -- headers: " + thCount + " < values: " + tvCount);
		}
		return tvCount > thCount;
	}
}