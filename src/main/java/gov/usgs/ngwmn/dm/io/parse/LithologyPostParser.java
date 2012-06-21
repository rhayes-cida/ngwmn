package gov.usgs.ngwmn.dm.io.parse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LithologyPostParser extends WaterPortalPostParser {
	
	// default instances
	private static Element DepthFrom = new Element("depthFrom", "depthTo", "LithologyDepthFrom");
	private static Element DepthTo   = new Element("depthTo",   "depthTo", "LithologyDepthTo");
	private static String CoordinateFullName;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public LithologyPostParser(String[] removeCols, Map<String, String> renameCols) {
		super(removeCols, renameCols);
	}

	
	@Override
	public List<Element> refineHeaderColumns(Collection<Element> original) {
		List<Element> headers = super.refineHeaderColumns(original);
		
		// we need to manage the headers - remove coordinates and add the new depth headers
		if ( ! headers.contains(DepthFrom) ) {
			for (Element element : headers) {
				if ( element.displayName.equalsIgnoreCase("coordinates") ) {
					Element coordinates = element;
					coordinates.hasChildren = true; // this causes it to not be displayed because the new cols are like its children
					CoordinateFullName = coordinates.fullName;
					if ( ! DepthFrom.fullName.equals( coordinates.fullName ) ) {
						DepthFrom = new Element(coordinates.fullName+"/depthFrom", "depthFrom", "LithologyDepthFrom");
						DepthTo   = new Element(coordinates.fullName+"/depthTo", "depthTo", "LithologyDepthTo");
					}
					headers.add(DepthFrom);
					headers.add(DepthTo);
					break;
				}
			}
		}
		return headers;
	}
	
	
	// custom refinement
	@Override
	public void refineDataColumns(Map<String, String> data) {
		super.refineDataColumns(data);
		
		splitCoordinatesIntoDepth(data);
	}

	// Split the 'coordinates' column into 'LithologyDepthFrom' and 'LithologyDepthTo',
	// a space is the current delimiter
	private void splitCoordinatesIntoDepth(Map<String, String> data) {
		
		if (CoordinateFullName == null) {
			logger.warn("splitCoordinatesIntoDepth has not parsed coordinates field.");
			return;
		}
		
		// then we need to manage the coordinate data 
		String oldValue = data.remove(CoordinateFullName); 
		if (oldValue != null) {
			try {
				String newValues[] = oldValue.split("\\s+");
				data.put(DepthFrom.fullName, newValues[0]);
				data.put(DepthTo.fullName,   newValues[1]);
			} catch (Exception e) {
				// this means that the value was not well formated
				data.put("LithologyDepthFrom", oldValue);
			}
		}
	}
}
