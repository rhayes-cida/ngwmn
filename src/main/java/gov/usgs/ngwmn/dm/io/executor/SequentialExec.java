package gov.usgs.ngwmn.dm.io.executor;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.OutputStream;

public class SequentialExec implements Executee {
	ExecFactory 		factory;
	Iterable<Specifier> specifiers;
	OutputStream    	output;
    
    public SequentialExec(ExecFactory fac, Iterable<Specifier> specs, OutputStream out) {
    	factory    = fac;
    	specifiers = specs;
    	output     = out;
    }
    
    @Override
    public Void call() throws Exception {
        for (Specifier spec : specifiers) {
        	Executee exec = factory.makeExecutor(spec, output);
        	try {
	        	exec.call();
        	} catch (Exception problem) {
        		if ( ! handleErrors(exec, problem) ) {
        			throw problem;
        		}
        	}
        }
        return null;
    }
    
    public boolean handleErrors(Executee exec, Exception problem) {
    	// default error handler
    	return true;
    }
    
}