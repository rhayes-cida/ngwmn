package gov.usgs.ngwmn.dm.io.aggregate;

import gov.usgs.ngwmn.dm.io.EntryDescription;
import gov.usgs.ngwmn.dm.io.FilenameEntry;
import gov.usgs.ngwmn.dm.io.JoiningSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.transform.TransformSupplier;
import gov.usgs.ngwmn.dm.spec.Encoding;
import gov.usgs.ngwmn.dm.spec.Specification;

import java.io.OutputStream;


public class SequentialJoiningAggregator extends SequentialFlowAggregator {

    protected Encoding encode;
    protected Iterable<Specification> specifications;
	
    public SequentialJoiningAggregator(FlowFactory fac, Iterable<Specification> specs,
    		Supplier<OutputStream> out, Encoding encoding) {
    	super(fac, null, out);
    	specifications = specs;
    	encode = encoding; // TODO maybe this should go in the specification
    }
    
    @Override
    public Void call() throws Exception {
    	Flow exec = null;
    	boolean threw = true;
    	try {
        	output.begin();
        	        	
	        for (Specification spec : specifications) {
	        	logger.info("Getting well data for {}", spec);
	        	
	        	EntryDescription desc = new FilenameEntry( spec.getDataType().toString() );
	        	desc.extension( encode.extension() ); // TODO I would like to see this in the transform but the zip entry is made sooner at the moment and might not have to be.
	        	Supplier<OutputStream> substream  = output.makeEntry(desc);
	        	substream = new TransformSupplier(substream, spec.getDataType(), encode);
	        	substream = new JoiningSupplier<OutputStream>(substream);
	        	
	        	Flow inner = new SequentialFlowAggregator(factory, spec.getWellIDs(), substream);
	        	inner.call();
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
    
}