package gov.usgs.ngwmn.dm.io.executor;

import gov.usgs.ngwmn.dm.cache.Specifier;
import gov.usgs.ngwmn.dm.io.PipeFactory;
import gov.usgs.ngwmn.dm.io.Pipeline;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelExec implements Callable<Void> {
    List<Pipeline> pipes;
    int parallex = 1;
    
    public ParallelExec(Iterable<Specifier> specs, PipeFactory fac) {
        for (Specifier s : specs) {
            pipes.add( fac.makePipe(s) );
        }
    }
    
    @Override
    public Void call() throws InterruptedException {
        ExecutorService x = Executors.newFixedThreadPool(parallex);
        
        List<Future<Void>> futures = x.invokeAll(pipes);
        
        for (Future<Void> future : futures) {
          try {
			future.get();
		  } catch (ExecutionException e) {
			  // TODO what to do here - no I do not intend this to go to production this way
		  }
        }
        return null;
    }
    
}
