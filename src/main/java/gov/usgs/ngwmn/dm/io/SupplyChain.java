package gov.usgs.ngwmn.dm.io;

import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.Closeable;
import java.io.IOException;

public class SupplyChain<T extends Closeable> extends Supplier<T> {

	private Supplier<T> link;
	
	public SupplyChain() {
		
	}
	public SupplyChain(Supplier<T> upstream) {
		link = upstream;
	}

	/**
	 * Requires the supplier that this intercepts and is linked.
	 * 
	 * For example, a ZipOutputStream could augment the the current
	 * stream to package up multiple files. 
	 * 
	 * @param supplier the supplier this chain is linked.
	 */
	public void setSupply(Supplier<T> supply) {
		if (supply == null) {
			throw new NullPointerException("Supplier for chain may not be null.");
		}
		if (link != null) {
			throw new RuntimeException("Supplier for chain may not be set multiple times.");
		}
		link = supply;
	}
	
	/**
	 * Default implementation does nothing - hence abstract.
	 * 
	 * Subclasses will get the stream from the chain link
	 * augment it appropriately and return a new stream
	 */
	@Override
	public T makeSupply(Specifier spec) throws IOException {
		return link.begin(spec);
	}
	
	/**
	 * override-able default impl that ensures the link receives the end signal.
	 */
	@Override
	public void end(boolean threw) throws IOException {
		link.end(threw);
	}
}
