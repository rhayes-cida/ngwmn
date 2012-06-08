package gov.usgs.ngwmn.dm.io.aggregate;

import gov.usgs.ngwmn.dm.io.EntryDescription;
import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.SpecifierEntry;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequentialFlowAggregator implements Flow {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected FlowFactory 		     factory;
	protected Iterable<Specifier>    specifiers;
	protected Supplier<OutputStream> output;
    
    public SequentialFlowAggregator(FlowFactory fac, Iterable<Specifier> specs, Supplier<OutputStream> out) {
    	factory    = fac;
    	specifiers = specs;
    	output     = out;
    }
    public SequentialFlowAggregator(FlowFactory fac, Iterable<Specifier> specs, OutputStream out) {
    	this(fac,specs, new SimpleSupplier<OutputStream>(out));
    }

    
    @Override
    public Void call() throws Exception {
    	Flow exec = null;
    	boolean threw = true;
    	try {
    		output.begin();
    		
	        for (Specifier spec : specifiers) {
	        	logger.info("Getting well data for {}", spec);
	        	EntryDescription entryDesc = new SpecifierEntry(spec);
	        	Supplier<OutputStream> substream = output.makeEntry(entryDesc);
	        	
	        	exec = factory.makeFlow(spec, substream);
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
    
    
    public boolean handleErrors(Flow exec, Exception problem) {
    	logger.error("Error processing sequential joined flows", problem);
    	return false; // false means "not handled" and should be re-thrown
    }
    
}