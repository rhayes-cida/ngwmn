package gov.usgs.ngwmn.dm.eventbus;

import java.util.List;

import com.google.common.eventbus.EventBus;

public class EventBusSpring extends EventBus {

	public EventBusSpring() {
		super();
	}

	public EventBusSpring(String identifier) {
		super(identifier);
	}

	public void setSubscribers(List<Object> ss) {
		for (Object o : ss) {
			this.register(o);
		}
	}
	
}
