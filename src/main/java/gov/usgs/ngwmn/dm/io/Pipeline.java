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
		
	public void setInputSupplier(Supplier<InputStream> supply) {
		iss = supply;
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
		try {
			InputStream  is = iss.get(spec);
			OutputStream os = oss.get(spec);
			invoker.invoke(is,os, statistics);
			statistics.markEnd(Status.DONE);
			logger.info("Done stats={}", statistics);
		} catch (IOException ioe) {
			statistics.markEnd(Status.FAIL);
			setException(ioe);
			logger.info("Fail stats={}", statistics);
			throw ioe;
		}
	}
	
	public PipeStatistics getStatistics() {
		return statistics;
	}
}
