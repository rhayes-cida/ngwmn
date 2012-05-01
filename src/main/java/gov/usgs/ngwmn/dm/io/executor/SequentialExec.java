package gov.usgs.ngwmn.dm.io.executor;

import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequentialExec implements Executee {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	protected ExecFactory 		     factory;
	protected Iterable<Specifier>    specifiers;
	protected Supplier<OutputStream> output;
    
    public SequentialExec(ExecFactory fac, Iterable<Specifier> specs, Supplier<OutputStream> out) {
    	factory    = fac;
    	specifiers = specs;
    	output     = out;
    }
    public SequentialExec(ExecFactory fac, Iterable<Specifier> specs, OutputStream out) {
    	this(fac,specs, new SimpleSupplier<OutputStream>(out));
    }

    
    @Override
    public Void call() throws Exception {
    	Executee exec = null;
    	boolean threw = true;
    	try {
    		// this is only done to show the pairing with end.
    		output.begin(null); // TODO since it is not needed - this is contrived
    		
	        for (Specifier spec : specifiers) {
	        	logger.info("Getting well data for {}", spec);
	        	exec = factory.makeExecutor(spec, output);
	        	exec.call();
	        }
        	threw = false;
		} catch (Exception problem) {
			if ( ! handleErrors(exec, problem) ) {
				throw problem;
			}
		} finally {
			output.end(threw);
		}
        return null;
    }
    
    public boolean handleErrors(Executee exec, Exception problem) {
    	// default error handler
    	return false;
    }
    
}