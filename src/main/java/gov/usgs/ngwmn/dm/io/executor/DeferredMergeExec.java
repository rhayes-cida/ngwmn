package gov.usgs.ngwmn.dm.io.executor;

import gov.usgs.ngwmn.dm.cache.Specifier;

import java.io.OutputStream;

public class DeferredMergeExec implements Executee {
	Iterable<Specifier> specifications;
    OutputStream        mergedOutputStream;
    ExecFactory     factory;
    
    public DeferredMergeExec(ExecFactory fac, Iterable<Specifier> specs, OutputStream out) {
    	specifications = specs;
    	factory = fac;
    	mergedOutputStream = out;
    }
    
    public Void call() throws Exception {
        for (Specifier spec : specifications) {
            // Need to handle deferred errors
        	Executee pipe = factory.makeExecutor(spec, mergedOutputStream);
            pipe.call();
        }
        return null;
    }
    
}
