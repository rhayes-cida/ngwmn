package gov.usgs.ngwmn.dm.prefetch;

import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class PrefetchLogFilter extends Filter<ILoggingEvent> {

	@Override
	public FilterReply decide(ILoggingEvent event) {
		String logname = event.getLoggerName();
		if (logname.contains("Prefetch")) {
			return FilterReply.ACCEPT;
		}
		String prefetch = event.getMDCPropertyMap().get("prefetch");
		if (prefetch == null) {
			return FilterReply.DENY;
		}
		return FilterReply.NEUTRAL;
	}

}
