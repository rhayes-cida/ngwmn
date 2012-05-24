package gov.usgs.ngwmn.dm.parse;


class Element {
	public static final char SEPARATOR = '/';

	final String  fullName;
	final String  localName;
	      String  displayName;
	      boolean hasChildren;

	public Element(String full, String local, String displayName) {
		this.fullName    = full;
		this.localName   = local;
		this.displayName = (displayName == null) ? local: displayName;
	}

	@Override
	public int hashCode() {
		return fullName.hashCode();
	};

	@Override
	public String toString() {
		return fullName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( ! (obj instanceof Element) ) { // null is implicitly checked with !instanceOf
			return false;
		}
		Element that = (Element) obj;
		return fullName.equals(that.fullName);
	}

	public void addParentToDisplayName() {
		displayName = parseQualifiedName(this.fullName, this.displayName);
	}
	
	
	/**
	 * Changed from one argument to two.  Quickfix: add null to second arguement. 
	 * @param fullName
	 * @return parent_name/item_name, e.g. parseQualifiedName("/WQX/Organization/Activity") == "Organization/Activity"
	 */
	public static String parseQualifiedName(String fullName, String current) {
		String result = fullName;
		current = (current == null) ? "" : current;
		try {
			int lastPos = fullName.lastIndexOf(SEPARATOR + current);
			int nextToLastPos = fullName.lastIndexOf(SEPARATOR, lastPos - 1);
			result = fullName.substring(nextToLastPos + 1);
		} catch (Exception e) {
			System.err.println("Tried to parseQualifiedName for [" + fullName + "] " + e.getMessage());
			//log.error("Tried to parseQualifiedName for [" + fullName + "]", e);
		}
		return result;
	}

	/**
	 * Returns true if parentUrl1 is an ancestor of DescendantUrl2
	 * @param ancestorUrl1
	 * @param descendantUrl2
	 * @return
	 */
	public static boolean isAncestorOf(String ancestorUrl1, String descendantUrl2) {
		assert(ancestorUrl1 != null && descendantUrl2 != null);
		return ancestorUrl1.length() < descendantUrl2.length()
				&& descendantUrl2.startsWith(ancestorUrl1 + "/");
	}
}