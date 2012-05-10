package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.NotImplementedException;
import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.io.executor.ExecFactory;
import gov.usgs.ngwmn.dm.io.executor.Executee;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PipelineAggregate extends Pipeline {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	SupplyInputAggregate aggregatedSupply;
	Iterable<Specifier> specs;
	
	public PipelineAggregate(Specifier sp) {
		super(sp);
		throw new NotImplementedException();
	}
	
    public PipelineAggregate(ExecFactory fac, Iterable<Specifier> specs, Supplier<OutputStream> out) {
    	super(null);
    	this.specs = specs;
    	setOutputSupplier(out);
    	aggregatedSupply = new SupplyInputAggregate(fac,specs,out);
    	setInputSupplier(aggregatedSupply);
    }

    public boolean handleErrors(Executee exec, Exception problem) {
    	// default error handler
    	return false;
    }
    
	@Override
	public void invoke() throws IOException {
		
    	boolean threw = true;
    	Supplier<OutputStream> output = getOutputSupplier();
    	
    	try {
    		logger.info("invoke for aggregated stream container -- begin");
    		output.begin();
    		
	        for (Specifier spec : specs) {
	        	// both the supply and this need the specs list and this loop just ensures we execute for each list entry
	        	try {
	        		logger.info("invoke for aggregated stream entry being for {}", spec);
	        		threw = true;
					aggregatedSupply.begin();
					aggregatedSupply.invoke(null,null,null); // no params used
	        		threw = false;
	        	} catch (RuntimeException e) {
	        		logger.error("Error:", e); // just so I can see the initial cause of failure
	        		throw e;
				} finally {
	        		logger.info("invoke for aggregated stream entry end");
					aggregatedSupply.end(threw);
				}
	        }
		} finally {
    		logger.info("invoke for aggregated stream container -- end");
			output.end(threw);
		}
	}
	    
}

class SupplyInputAggregate extends Supplier<InputStream> implements Invoker {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ExecFactory fac;
	private Iterator<Specifier> specs;
	protected Supplier<OutputStream> out;
	private Pipeline pipe;
	
	private final InputStream marker = new InputStream() {
		@Override
		public int read() throws IOException {
			// TODO Auto-generated method stub
			return 0;
		}
	};
	
	public SupplyInputAggregate(ExecFactory factory, Iterable<Specifier> specifiers, Supplier<OutputStream> output) {
		fac   = factory;
		specs = specifiers.iterator();
		out   = output;
	}

	@Override
	public InputStream initialize() throws IOException {
		Specifier spec = specs.next();
    	logger.info("initialize - constructing pipeline well data for {}", spec);
    	Supplier<OutputStream> entrySupplier = out.makeEntry(spec);
		pipe = (Pipeline) fac.makeExecutor(spec, entrySupplier);
		
		// this is just a marker to obey the contract - it only used to ensure proper begin-end pairing
		return marker;
	}

	@Override
	public void invoke(InputStream is, OutputStream os, PipeStatistics stats) throws IOException {
		logger.info("invoke for aggregated stream entry -- invoke");
		pipe.invoke();
	}
	
	@Override
	public void end(boolean threw) throws IOException {
		logger.info("end for aggregated stream entry -- end");
		super.end(threw);
	}
}
