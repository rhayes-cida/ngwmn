package gov.usgs.ngwmn.dm.io.executor;

import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelExec implements Executee {
	ExecFactory 		   factory;
	Iterable<Specifier>    specifiers;
	Supplier<OutputStream> output;
	
	int parallex = 1;
    
    public ParallelExec(ExecFactory fac, Iterable<Specifier> specs, OutputStream out) {
    	factory    = fac;
    	specifiers = specs;
    	output     =  new SimpleSupplier<OutputStream>(out);
    }
    public ParallelExec(ExecFactory fac, Iterable<Specifier> specs, OutputStream out, int parallelism) {
    	this(fac, specs, out);
    	parallex = parallelism;
    }
    
    @Override
    public Void call() throws Exception {
    	List<Executee> execs = new LinkedList<Executee>();
        for (Specifier spec : specifiers) {
        	execs.add( factory.makeExecutor(spec, output) );
        }
        
        ExecutorService exec = Executors.newFixedThreadPool(parallex);
        
        List<Future<Void>> futures = exec.invokeAll(execs);
        
        for (Future<Void> future : futures) {
			future.get(); // Since our current impl is Callable<Void> this just waits for finish
        }
        return null;
    }
    
}
