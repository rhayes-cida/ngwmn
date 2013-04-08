package gov.usgs.ngwmn.dm.spec;

import gov.usgs.ngwmn.WellDataType;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A specifier with potentially more complex request parameters that must be 
 * resolved into individual specifiers
 * 
 * @author david
 *
 */
public class Specification {
	
	private Encoding encode = Encoding.CSV; // TODO is this the default encoding for now?

	private boolean bundled = true; // TODO true by default for now until we decide we would like offer unbundled
	
	private Date beginDate;
	private Date endDate;
	
	private final EnumSet<WellDataType> dataTypes;
	// Delineated list of agency well IDs
	private final Map<WellDataType,List<Specifier>> wellIDs;
	private final Set<Specifier> knownWells;
	
	public Specification() {
		dataTypes = EnumSet.noneOf(WellDataType.class);
		wellIDs   = new HashMap<WellDataType, List<Specifier>>();
		for (WellDataType type : WellDataType.values()) {
			wellIDs.put(type, new LinkedList<Specifier>());
		}
		knownWells = new HashSet<Specifier>();
	}
	
	public List<Specifier> getWellIDs(WellDataType type) {
		return wellIDs.get(type);
	}
	public boolean addWell(Specifier spec) {
		WellDataType type = spec.getTypeID();
		dataTypes.add(type);
		
		spec.setBeginDate(beginDate);
		spec.setEndDate(endDate);
		spec.setEncoding(getEncode());
		
		if (knownWells.add(spec)) {
			wellIDs.get(type).add(spec);
			return true;
		}
		else {
			return false;
		}
	}
	
	public Collection<WellDataType> getDataTypes() {
		return dataTypes;
	}
	
	public int size() {
		int size = 0;
		for (WellDataType type : dataTypes) {
			size += wellIDs.get(type).size();
		}
		return size; 
	}
	
	public boolean isEmpty() {
		boolean empty = true;
		
		for (WellDataType type : dataTypes) {
			empty &= wellIDs.get(type).isEmpty();
		}
		
		return empty;
	}
	
	public int getWellTotalSize() {
		int size = 0;
		
		for (WellDataType type : dataTypes) {
			size += wellIDs.get(type).size();
		}
		
		return size;
	}
	

	public Encoding getEncode() {
		return encode;
	}
	public void setEncode(Encoding encode) {
		this.encode = encode;
		for (Specifier s: knownWells) {
			s.setEncoding(encode);
		}
	}

	public boolean isBundled() {
		return bundled;
	}
	public void setBundled(boolean bundled) {
		this.bundled = bundled;
	}

	public void setBeginDate(Date date) {
		beginDate = date;
		for (Specifier s: knownWells) {
			s.setBeginDate(date);
		}
	}
	
	public void setEndDate(Date date) {
		endDate = date;
		for (Specifier s: knownWells) {
			s.setEndDate(date);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Specification [encode=");
		builder.append(encode);
		builder.append(", bundled=");
		builder.append(bundled);
		builder.append(", beginDate=");
		builder.append(beginDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", dataTypes=");
		builder.append(dataTypes);
		builder.append(", knownWells=");
		builder.append(knownWells);
		builder.append("]");
		return builder.toString();
	}
	

}
