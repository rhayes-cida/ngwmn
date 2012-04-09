package gov.usgs.ngwmn.dm.io.executor;

import gov.usgs.ngwmn.dm.cache.Specifier;

import java.io.OutputStream;

public class SequentialExec implements Executee {
	ExecFactory factory;
	OutputStream    output;
	Iterable<Specifier> specifiers;
    
    public SequentialExec(ExecFactory fac, Iterable<Specifier> specs, OutputStream out) {
    	factory = fac;
    	output  = out;
    	specifiers = specs;
    }
    
    @Override
    public Void call() throws Exception {
        for (Specifier spec : specifiers) {
        	Executee exec = factory.makeExecutor(spec, output);
        	exec.call();
        }
        return null;
    }
}