package gov.usgs.ngwmn.dm.prefetch;

import gov.usgs.ngwmn.dm.cache.Cleaner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class PrefetchController {

	private ThreadPoolTaskScheduler sked;
	private Prefetcher prefetcher;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Cleaner cleaner;
	
	@Autowired
	private ApplicationContext ctx;
	public void setApplicationContext(ApplicationContext ctx) {
		this.ctx = ctx;
	}

	@Autowired
	private MBeanExporter mbeanExporter;
	
	/**
	 * Stop any active prefetch and prevent any subsequent starts.
	 */
	public void stop() {
		logger.info("Stopping");
		prefetcher.requestStop(null);
		
		//sked.setWaitForTasksToCompleteOnShutdown(false);
		//sked.shutdown();
		sked.getScheduledExecutor().shutdownNow();
	}
	
	private void cleanCache() {
		if (cleaner != null) {
			int ct = cleaner.clean();
			logger.info("Cleaned {} old cache entries", ct);
		} else {
			logger.info("No cleaner configured");
		}
	}

	/**
	 * Start the prefetch job immediately, without any scheduling.
	 */
	public void start() {
		logger.info("Starting");
		
		cleanCache();

		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				prefetcher.allowRun();
				prefetcher.call();
			}
		};
		
		// TODO clean out scheduled jobs?
		// Would be best to remove all previously scheduled jobs, but that's not easy to do from here.
		// ThreadPoolExecutor has some useful methods, if the Executor used by the scheduler is in
		// fact that kind of executor.
		
		// This will fail if the scheduler is not enabled;
		// because enabling the scheduler can have far-reaching effects,
		// leave that decision to the user.
		sked.execute(task);
	}
	
	private List<Future<PrefetchOutcome>> multithreadOutcomes = new ArrayList<Future<PrefetchOutcome>>();
	
	private Prefetcher makePrefetcher() {
		return ctx.getBean("PrefetchInstance", Prefetcher.class);
	}
	
	public synchronized List<Future<PrefetchOutcome>>  startInParallel() {
		logger.info("Start in parallel");
		List<String> agencies = prefetcher.agencyCodes();
		
		cleanCache();
		
		for (final String agency : agencies) {
			logger.info("Launching for {}", agency);
			
			MDC.put("agency", agency);
			final Map<?,?> mdc = MDC.getCopyOfContextMap();
			Callable<PrefetchOutcome> task = new Callable<PrefetchOutcome>() {

				@Override
				public PrefetchOutcome call() throws Exception {
					MDC.setContextMap(mdc);
					try {
						Prefetcher pf = makePrefetcher();
						if (mbeanExporter != null) {
							mbeanExporter.registerManagedResource(pf,new ObjectName("ngwmn.prefetcher", "agency", agency));
						}
						return pf.callForAgency(agency);	
					} finally {
						MDC.clear();
					}
				}
				
			};
			
			multithreadOutcomes.add(sked.submit(task));
		}
		
		logger.info("Launched {} tasks", multithreadOutcomes.size());
		if (logger.isDebugEnabled()) {
			for (Future<PrefetchOutcome> f : multithreadOutcomes) {
				logger.debug("future {} done: {}",f, f.isDone());
			}
		}
		
		return multithreadOutcomes;
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
		sked.setWaitForTasksToCompleteOnShutdown(true);
	}
	
	public String getSchedulerClassname() {
		return sked.getClass().toString();
	}

	public Prefetcher getPrefetcher() {
		return prefetcher;
	}

	public void setPrefetcher(Prefetcher prefetcher) {
		this.prefetcher = prefetcher;
	}

	public Cleaner getCleaner() {
		return cleaner;
	}

	public void setCleaner(Cleaner cleaner) {
		this.cleaner = cleaner;
	}

	public MBeanExporter getMbeanExporter() {
		return mbeanExporter;
	}

	public void setMbeanExporter(MBeanExporter mbeanExporter) {
		this.mbeanExporter = mbeanExporter;
	}
	
}

