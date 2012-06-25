package gov.usgs.ngwmn.dm.io.parse;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoordinatePostParser implements PostParser {
	private final transient Logger logger = LoggerFactory.getLogger(getClass());
	
	private final String  coordinateFullName;
	private final String  displayPrefix;
	private final Element depthFrom;
	private final Element depthTo;
	
	public CoordinatePostParser(String elementFullName, String displayPrefix) {
		
		coordinateFullName = elementFullName;
		this.displayPrefix = displayPrefix;
		depthFrom = new Element(coordinateFullName+"/depthFrom", "depthFrom", displayPrefix+"DepthFrom");
		depthTo   = new Element(coordinateFullName+"/depthTo",   "depthTo",   displayPrefix+"DepthTo");
	}

	
	@Override
	public List<Element> refineHeaderColumns(List<Element> headers) {
		
		logger.trace("Coordinate-Depth specific header refinements");
		
		// we need to manage the headers - remove coordinates and add the new depth headers
		if ( ! headers.contains(depthFrom) ) {
			for (Element element : headers) {
				if ( element.fullName.equalsIgnoreCase(coordinateFullName) ) {
					logger.trace("replacing coordinates with {} depth columns", displayPrefix);
					
					Element coordinates = element;
					coordinates.hasChildren = true; // this causes it to not be displayed because the new cols are like its children
					int index = headers.indexOf(coordinates);
					headers.add(index, depthTo);
					headers.add(index, depthFrom);
					break;
				}
			}
		}
		return headers;
	}
	
	
	// custom refinement
	@Override
	public void refineDataColumns(Map<String, String> data) {
		logger.trace("Coordinate-Depth specific data refinements");
		splitCoordinatesIntoDepth(data);
	}

	// Split the 'coordinates' column into 'LithologyDepthFrom' and 'LithologyDepthTo',
	// a space is the current delimiter
	private void splitCoordinatesIntoDepth(Map<String, String> data) {
		
		// then we need to manage the coordinate data 
		String oldValue = data.remove(coordinateFullName);
		
		if (oldValue != null) {
			try {
				String[] newValues = oldValue.split("\\s+");
				data.put(depthFrom.fullName, newValues[0]);
				data.put(depthTo.fullName,   newValues[1]);
			} catch (Exception e) {
				// this means that the value was not well formated
				data.put(depthFrom.fullName, oldValue);
			}
		}
	}
	
	@Override
	public Set<String> getRemoveColumns() {
		// no remove columns
		return new HashSet<String>();
	}
}
