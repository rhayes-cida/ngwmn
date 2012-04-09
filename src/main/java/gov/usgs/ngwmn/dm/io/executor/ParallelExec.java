package gov.usgs.ngwmn.dm.io.executor;

import gov.usgs.ngwmn.dm.cache.Specifier;

import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelExec implements Executee {
	ExecFactory factory;
	OutputStream    output;
	Iterable<Specifier> specifiers;
	int parallex = 1;
    
    public ParallelExec(ExecFactory fac, Iterable<Specifier> specs, OutputStream out) {
    	factory = fac;
    	output  = out;
    	specifiers = specs;
    }
    public ParallelExec(ExecFactory fac, Iterable<Specifier> specs, OutputStream out, int parallelism) {
    	this(fac, specs, out);
    	parallex = parallelism;
    }
    
    @Override
    public Void call() throws Exception {
    	List<Executee> pipes = new LinkedList<Executee>();
        for (Specifier spec : specifiers) {
            pipes.add( factory.makeExecutor(spec, output) );
        }
        
        ExecutorService exec = Executors.newFixedThreadPool(parallex);
        
        List<Future<Void>> futures = exec.invokeAll(pipes);
        
        for (Future<Void> future : futures) {
			future.get();
        }
        return null;
    }
    
}
