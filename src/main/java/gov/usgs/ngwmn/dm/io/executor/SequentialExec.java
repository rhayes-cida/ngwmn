package gov.usgs.ngwmn.dm.io.executor;

import gov.usgs.ngwmn.dm.cache.Specifier;
import gov.usgs.ngwmn.dm.io.PipeFactory;
import gov.usgs.ngwmn.dm.io.Pipeline;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class SequentialExec implements Callable<Void>{
    List<Pipeline> pipes;
    
    SequentialExec(Iterable<Specifier> specs, PipeFactory fac) {
    	
    	pipes = new LinkedList<Pipeline>();
    	
        for (Specifier spec : specs) {
        	Pipeline pipe = fac.makePipe(spec);
            pipes.add(pipe);
        }
    }
    
    @Override
    public Void call() {
        for (Pipeline pipe : pipes) {
        	pipe.call();
        }
        return null;
    }
}