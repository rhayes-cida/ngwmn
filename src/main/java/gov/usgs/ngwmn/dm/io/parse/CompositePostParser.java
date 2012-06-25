package gov.usgs.ngwmn.dm.io.parse;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositePostParser implements PostParser {
	private final transient Logger logger = LoggerFactory.getLogger(getClass());

	protected final Set<PostParser> postParsers;
	
	public CompositePostParser() {
		postParsers = new HashSet<PostParser>();
	}

	public void addPostParser(PostParser postParser) {
		if (postParser != null) {
			postParsers.add(postParser);
		}
	}
	
	@Override
	public List<Element> refineHeaderColumns(List<Element> original) {
		logger.trace("Composite header refinements");

		List<Element> headers = original;

		for (PostParser postParser : postParsers) {
			headers = postParser.refineHeaderColumns(headers);
		}
		
		return headers;
	}
	
	
	// custom refinement
	@Override
	public void refineDataColumns(Map<String, String> data) {
		logger.trace("Composite data refinements");

		for (PostParser postParser : postParsers) {
			postParser.refineDataColumns(data);
		}
	}

}
