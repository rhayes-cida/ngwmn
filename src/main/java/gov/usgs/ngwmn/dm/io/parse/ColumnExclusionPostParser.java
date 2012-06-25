package gov.usgs.ngwmn.dm.io.parse;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnExclusionPostParser implements PostParser {
	private final transient Logger logger = LoggerFactory.getLogger(getClass());

	protected final Set<String>  removeColumns;
	
	public ColumnExclusionPostParser(Collection<String> removeCols) {
		if (removeCols==null) {
			removeColumns = new HashSet<String>();
		} else {
			removeColumns = new HashSet<String>(removeCols);
		}
	}
	public ColumnExclusionPostParser(String ... removeCols) {
		this( Arrays.asList(removeCols==null ? new String[]{} : removeCols) );
	}
	
	@Override
	public List<Element> refineHeaderColumns(List<Element> headers) {
		logger.trace("Column exclusion header refinements");
		
		List<Element> keepHeaders = new LinkedList<Element>();
		
		for (Element element : headers) {
			// two - retain only wanted cols
			if ( ! element.hasChildren 
					&& ! removeColumns.contains(element.fullName)
					&& ! removeColumns.contains(element.displayName)
					&& ! removeColumns.contains(element.localName)
					) {
				logger.trace("retaining element: {}", element.displayName);
				keepHeaders.add( element );
			} else {
				logger.trace("excluding element: {}", element.displayName);
			}
		}
		return keepHeaders;
	}

	@Override
	public void refineDataColumns(Map<String, String> data) {
		removeElements(data);
	}
	protected void removeElements(Map<String, String> data) {
		for (String name : removeColumns) {
			data.remove(name);
		}
	}

}
