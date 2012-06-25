package gov.usgs.ngwmn.dm.io.parse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnRenamePostParser implements PostParser {
	private final transient Logger logger = LoggerFactory.getLogger(getClass());

	protected final Map<String, String> renameColumns;
	
	public ColumnRenamePostParser(Map<String, String> renameCols) {
		renameColumns = new HashMap<String, String>();
		renameColumns.putAll(renameCols);
	}
	
	@Override
	public List<Element> refineHeaderColumns(List<Element> headers) {
		logger.trace("Column rename header refinements");
		
		for (Element element : headers) {
			// three - rename cols
			if ( renameColumns.containsKey( element.displayName ) ) {
				logger.trace("renaming  element: from '{}' to '{}'", element.displayName, renameColumns.get( element.displayName ));
				element.displayName = renameColumns.get( element.displayName );
			}
		}
		return headers;
	}

	@Override
	public void refineDataColumns(Map<String, String> data) {
		// no data refinements
	}

	public void addColumn(String col, String value) {
		renameColumns.put(col, value);
	}

	@Override
	public Set<String> getRemoveColumns() {
		// no remove columns
		return new HashSet<String>();
	}

}
