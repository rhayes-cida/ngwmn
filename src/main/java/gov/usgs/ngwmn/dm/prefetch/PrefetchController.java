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
		prefetcher.requestStop(null);
		
		//sked.setWaitForTasksToCompleteOnShutdown(false);
		//sked.shutdown();
		sked.getScheduledExecutor().shutdownNow();
	}
	
	/**
	 * Start the prefetch job immediately, without any scheduling.
	 */
	public void start() {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				prefetcher.allowRun();
				prefetcher.call();
			}
		};
		
		// TODO clean out scheduled jobs?
		// Would be best to remove all previously scheduled jobs, but that's not easy to do from here.
		// ThreadPoolExecutor has some useful methods, if the Executor used by the cheduler is in
		// fact that kind of executor.
		
		// This will fail if the scheduler is not enabled;
		// because enabling the scheduler can have far-reaching effects,
		// leave that decision to the user.
		sked.execute(task);
	}
	
	/**
	 * Enable scheduling of the prefetch job -- may or may not start a prefetch job immediately,
	 * depending on schedule and time.
	 */
	public void enable() {
		prefetcher.allowRun();
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

