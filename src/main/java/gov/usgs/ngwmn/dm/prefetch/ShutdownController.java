package gov.usgs.ngwmn.dm.prefetch;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class ShutdownController implements ApplicationListener<ContextClosedEvent> {

	private ThreadPoolTaskScheduler sked;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public void setScheduler(ThreadPoolTaskScheduler sked) {
		this.sked = sked;
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		if (sked == null) {
			logger.warn("No sked?");
			return;
		}
		
		ScheduledExecutorService svc = sked.getScheduledExecutor();
		logger.info("Shutting down scheduler {} executor {}", sked, svc);
		if (svc == null) {
			logger.warn("Highly unusual circumstance, null executor for {}", sked);
		} 
		else if (svc.isTerminated()) {
			logger.info("Already terminated {}", svc);
		}
		else {
			svc.shutdown();
			try {
				boolean clean = svc.awaitTermination(60, TimeUnit.SECONDS);
				if (clean) {
					logger.info("Clean termination of {}", sked);
				} else {
					logger.warn("Reached time limit waiting for termination of {}", sked);
				}
			} catch (InterruptedException ie) {
				logger.warn("Interrupted in shutdown", ie);
			}
		}
		sked.destroy();
	}
	
}
