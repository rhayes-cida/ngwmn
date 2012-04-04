package gov.usgs.ngwmn.dm.io;


import gov.usgs.ngwmn.dm.cache.PipeStatistics;
import gov.usgs.ngwmn.dm.cache.PipeStatistics.Status;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;


public class Pipeline implements Callable<Void> {
	private Invoker invoker;
	private Supplier<InputStream>  iss;
	private Supplier<OutputStream> oss;
	private PipeStatistics statistics = new PipeStatistics();
	private IOException    ioe;
	
	
	public void setInputSupplier(Supplier<InputStream> supply) {
		iss = supply;
	}
	
	public void setOutputSupplier(Supplier<OutputStream> supply) {
		oss = supply;
	}
	public Supplier<OutputStream> getOutputSupplier() {
		return oss;
	}
	
	public void addOutputSupplier(SupplyOutput supply) {
		
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
	
	public void invoke() throws IOException {
		statistics.markStart();
		try {
			InputStream  is = iss.get();
			OutputStream os = oss.get();
			invoker.invoke(is,os, statistics);
			statistics.markEnd(Status.DONE);
		} catch (IOException ioe) {
			statistics.markEnd(Status.FAIL);
			throw ioe;
		}
	}
	
	public PipeStatistics getStatistics() {
		return statistics;
	}
}
