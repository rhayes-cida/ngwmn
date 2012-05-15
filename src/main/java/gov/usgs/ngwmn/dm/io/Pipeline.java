package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.io.executor.Executee;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pipeline implements Executee {
	
	private static Logger logger = LoggerFactory.getLogger(Pipeline.class);	

	private Supplier<InputStream>  iss;
	private Supplier<OutputStream> oss;
	private IOException    ioe;
	private Invoker        invoker;
	
	private final Specifier      spec;
	
	
	public Pipeline(Specifier sp) {
		spec = sp;
		invoker    = new CopyInvoker(); // it makes nice to have a default impl
	}
		
	
	public Specifier getSpecifier() {
		return spec;
	}


	public void setInputSupplier(Supplier<InputStream> supply) {
		iss = supply;
	}
	public Supplier<InputStream> getInputSupplier() {
		return iss;
	}

	public void setOutputSupplier(Supplier<OutputStream> supply) {
		oss = supply;
	}
	public Supplier<OutputStream> getOutputSupplier() {
		return oss;
	}
	
	public void addOutputSupplier(Supplier<OutputStream> supply) {
		
		if (oss != null) {
			supply = new SupplyTeeOutput(supply, oss);
		}
		
		setOutputSupplier(supply);
	}
	public void chainOutputSupplier(SupplyChain<OutputStream> supply) {
		// TODO this currently does not fail safe when oss is null as addOutputSupplier does
		supply.setSupply(oss);
		setOutputSupplier(supply);
	}
	
	public void setInvoker(Invoker invoke) {
		invoker = invoke;
	}
	
	public Void call() throws IOException {
		invoke();
		return null;
	}
	
	public boolean success() {
		return ioe == null;
	}
	
	public IOException getException() {
		return ioe;
	}
	public void setException(IOException ioe) {
		this.ioe = ioe;
	}

	public long invoke() throws IOException {
		InputStream  is = iss.begin();
		boolean threw = true;
		long ct = 0;
		try {
			try {
				OutputStream os = oss.begin();
				logger.debug("began oss={}, oss.source={}", oss, oss.getSource());
				ct = invoker.invoke(is,os);
				logger.debug("done with invoke, oss={}, oss.source={}", oss, oss.getSource());
				threw = false;
			} catch (IOException ioe) {
				setException(ioe);
				logger.warn("Fail message={}", ioe.getMessage());
				throw ioe;
			} finally {
				if (oss != null && oss.isInitialized()) {
					oss.end(threw);
				}
			}
		// TODO maybe a catch here too?!
		} finally {
			if (iss != null) {
				iss.end(threw);
			}
		}
		return ct;
	}
	
}
