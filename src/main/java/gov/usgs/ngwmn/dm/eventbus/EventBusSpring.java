package gov.usgs.ngwmn.dm.eventbus;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.EventBus;

public class EventBusSpring extends EventBus {

	private List<Object> subscribers = new ArrayList<Object>();
	
	public EventBusSpring() {
		super();
	}

	public EventBusSpring(String identifier) {
		super(identifier);
	}

	public void setSubscribers(List<Object> ss) {
		for (Object o : ss) {
			super.register(o);
			subscribers.add(o);
		}
	}
	
	public void shutdown() {
		while (! subscribers.isEmpty()) {
			Object sus = subscribers.remove(0);
			super.unregister(sus);
		}
	}
}
