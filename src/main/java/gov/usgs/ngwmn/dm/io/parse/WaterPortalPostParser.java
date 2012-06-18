package gov.usgs.ngwmn.dm.io.parse;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WaterPortalPostParser implements PostParser {

	protected final Map<String, String> constAdditionalCols;
	protected final Set<String> 		removeColumns;
	protected final Map<String, String> renameColumns;
	
	public WaterPortalPostParser(String[] removeCols, Map<String, String> renameCols) {
		constAdditionalCols = new LinkedHashMap<String, String>();
		
		removeColumns = new HashSet<String>();
		if (removeCols != null) {
			for (String removeCol : removeCols) {
				removeColumns.add(removeCol);
			}
		}
		
		renameColumns = new HashMap<String, String>();
		renameColumns.putAll(renameCols);
	}
	
	
	@Override
	public void refineDataColumns(Map<String, String> data) {
		removeIngnoreElements(data);
		appendConstElements(data);
	}

	public void removeIngnoreElements(Map<String, String> data) {
		for (String name : removeColumns) {
			data.remove(name);
//			String value = data.remove(name);
//			if (value != null) {
//				System.err.println("Removed key:'" +name+"' value:'"+value+"'");
//			}
		}
	}
	
	public void appendConstElements(Map<String, String> data) {
		for (String constCol : constAdditionalCols.keySet()) {
			data.put(constCol, constAdditionalCols.get(constCol));
		}
	}

	
	@Override
	public List<Element> refineHeaderColumns(Collection<Element> headers) {
		List<Element> modHeaders = new LinkedList<Element>();
		
		for (String constCol : constAdditionalCols.keySet()) {
			modHeaders.add( new Element(constCol, constCol, constCol) );
		}
		for (Element element : headers) {
			if ( ! element.hasChildren 
					&& ! removeColumns.contains(element.fullName)
					&& ! removeColumns.contains(element.displayName)
					&& ! removeColumns.contains(element.localName)
					) {
				modHeaders.add( element );
			}
			if ( renameColumns.containsKey( element.displayName ) ) {
				element.displayName = renameColumns.get( element.displayName );
			}
		}
		return modHeaders;
	}

	
	@Override // this method is for testing
	public Set<String> getRemoveColumns() {
		return Collections.unmodifiableSet(removeColumns);
	}
	
	@Override
	public void addConstColumn(String col, String string) {
		constAdditionalCols.put(col, string);
	}
	
	public void addRenameColumn(String col, String string) {
		renameColumns.put(col, string);
	}	
}
