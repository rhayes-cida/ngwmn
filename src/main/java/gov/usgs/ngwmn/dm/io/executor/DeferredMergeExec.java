package gov.usgs.ngwmn.dm.io.executor;

import gov.usgs.ngwmn.dm.cache.Specifier;
import gov.usgs.ngwmn.dm.io.PipeFactory;
import gov.usgs.ngwmn.dm.io.Pipeline;
import gov.usgs.ngwmn.dm.io.Supplier;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;

public class DeferredMergeExec implements Callable<Void> {
	Iterable<Specifier> specifications;
    OutputStream mergedOutputStream;
    PipeFactory factory;
    
    // FlowFactory will not be called until runtime
    // Might prefer to use an OutputStreamFactory
    public DeferredMergeExec(Iterable<Specifier> specs, PipeFactory fac, OutputStream mos) {
    	specifications = specs;
    	factory = fac;
    }
    
    public Void call() {
        for (Specifier spec : specifications) {
            // Need to handle deferred errors
            Pipeline pipe = factory.makePipe(spec);
            pipe.setOutputSupplier( new Supplier<OutputStream>() {
				
				@Override
				public OutputStream get() throws IOException {
					// TODO Auto-generated method stub
					return mergedOutputStream;
				}
			});
            
            pipe.call();
        }
        return null;
    }
    
}
