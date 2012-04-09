package gov.usgs.ngwmn.dm.io.executor;

import gov.usgs.ngwmn.dm.cache.Specifier;

import java.io.OutputStream;

public class DeferredMergeExec extends SequentialExec {
	
    public DeferredMergeExec(ExecFactory fac, Iterable<Specifier> specs, OutputStream out) {
		super(fac, specs, out);
	}

	public boolean handleErrors(Executee exec, Exception problem) {
    	// TODO differed merge error handler, false will interrupt subsequent calls
		return true;
    }

}
