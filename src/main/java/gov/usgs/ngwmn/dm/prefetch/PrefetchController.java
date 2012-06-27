package gov.usgs.ngwmn.dm.prefetch;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class PrefetchController {

	private ThreadPoolTaskScheduler sked;
	private Prefetcher prefetcher;
	
	/**
	 * Stop any active prefetch and prevent any subsequent starts.
	 */
	public void stop() {
		sked.setWaitForTasksToCompleteOnShutdown(false);
		sked.shutdown();
	}
	
	/**
	 * Start the prefetch job immediately, without any scheduling.
	 */
	public void start() {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				prefetcher.call();
			}
		};
		
		// What are valid states of sked for this call?
		// Do we have to check that it has not been shut down?
		sked.execute(task);
	}
	
	/**
	 * Enable scheduling of the prefetch job -- may or may not start a prefetch job immediately,
	 * depending on schedule and time.
	 */
	public void enable() {
		if (sked.getScheduledExecutor().isShutdown() ||
				sked.getScheduledExecutor().isTerminated()) {
			sked.initialize();
		}		
	}
	
	public boolean isEnabled() {
		return ! getScheduler().getScheduledExecutor().isShutdown();
	}
	
	public ThreadPoolTaskScheduler getScheduler() {
		return sked;
	}

	public void setScheduler(ThreadPoolTaskScheduler sked) {
		this.sked = sked;
	}

	public Prefetcher getPrefetcher() {
		return prefetcher;
	}

	public void setPrefetcher(Prefetcher prefetcher) {
		this.prefetcher = prefetcher;
	}
	
}

