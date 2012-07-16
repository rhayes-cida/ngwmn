package gov.usgs.ngwmn.dm.io.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FixedOrderPostParser 
	extends DefaultPostParser 
	implements PostParser
{
	
	private List<Element> myElements;
	
	public FixedOrderPostParser(String[][] columns) {
		super();
		
		myElements = new ArrayList<Element>(columns.length);
		
		for (String[] col : columns) {
			String fullPath = col[0];
			String localName = col[0]; // TODO Is there a better choice? Basename or something? Or null?
			String label = col[1];
			Element el = new Element(fullPath, localName, label);
			if (label == null) {
				el.hasChildren = true;
			}
			
			myElements.add(el);
		}
		
		myElements = Collections.unmodifiableList(myElements);
	}

	@Override
	public List<Element> refineHeaderColumns(List<Element> headers) {
		return myElements;
	}	

}
