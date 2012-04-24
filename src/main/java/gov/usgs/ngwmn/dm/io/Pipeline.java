package gov.usgs.ngwmn.dm.io;


import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatistics.Status;
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
	private PipeStatistics statistics;
	private IOException    ioe;
	private Invoker        invoker;
	
	private final Specifier      spec;
	
	
	public Pipeline(Specifier sp) {
		spec = sp;
		statistics = new PipeStatistics();
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
	
	public Void call() {
		
		try {
			invoke();
		} catch (IOException e) {
			// TODO this is a first attempt at handling - expect a refactor
			ioe = e;
		}
		
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

	public void invoke() throws IOException {
		statistics.markStart();
		InputStream  is = iss.get(spec);
		try {
			try {
				OutputStream os = oss.get(spec);
				invoker.invoke(is,os, statistics);
				statistics.markEnd(Status.DONE);
				logger.info("Done stats={}", statistics);
			} catch (IOException ioe) {
				statistics.markEnd(Status.FAIL);
				setException(ioe);
				logger.info("Fail stats={}", statistics);
				throw ioe;
			} finally {
				if (oss != null) {
					oss.end(spec);
				}
			}
		} finally {
			if (iss != null) {
				iss.end(spec);
			}
		}
	}
	
	public PipeStatistics getStatistics() {
		return statistics;
	}
}
