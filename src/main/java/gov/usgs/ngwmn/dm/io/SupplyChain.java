package gov.usgs.ngwmn.dm.io;

import java.io.IOException;

public abstract class SupplyChain<T> extends Supplier<T> {

	private Supplier<T> link;

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
		link = supply;
	}
	
	/**
	 * Default implementation does nothing - hence abstract.
	 * 
	 * Subclasses will get the stream from the chain link
	 * augment it appropriately and return a new stream
	 */
	@Override
	public T get() throws IOException {
		return link.get();
	}
}
