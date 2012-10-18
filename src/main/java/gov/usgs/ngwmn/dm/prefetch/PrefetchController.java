package gov.usgs.ngwmn.dm.prefetch;

import gov.usgs.ngwmn.dm.cache.Cleaner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
	private WaterlevelRankStatsWorker wlsWorker;
	
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
	public synchronized void start() {
		logger.info("Starting");
		
		cleanCache();
		MDC.put("prefetch", "single");
		final Map<?,?> mdc = MDC.getCopyOfContextMap();
		
		Callable<PrefetchOutcome> task = new Callable<PrefetchOutcome>() {

			@Override
			public PrefetchOutcome call() throws Exception {
				MDC.setContextMap(mdc);
				try {
					Prefetcher pf = makePrefetcher();
					if (mbeanExporter != null) {
						mbeanExporter.registerManagedResource(pf,new ObjectName("ngwmn.prefetcher", "agency", "all"));
					}
					return pf.call();
				} catch (Exception e) {
					logger.error("Problem making prefetcher", e);
					throw e;
				} finally {
					MDC.clear();
				}
			}
		};
		
		multithreadOutcomes.put("all",sked.submit(task));

	}
	
	// agency name is key, or "all" for all agencies
	private Map<String,Future<PrefetchOutcome>> multithreadOutcomes = 
			Collections.synchronizedMap(
					new TreeMap<String,Future<PrefetchOutcome>>());
	
	private Prefetcher makePrefetcher() {
		logger.trace("Context is {}", ctx);
		return ctx.getBean("PrefetchInstance", Prefetcher.class);
	}
	
	public synchronized Map<String, Future<PrefetchOutcome>>  startInParallel() {
		logger.info("Start in parallel");
		
		if ( ! multithreadOutcomes.isEmpty()) {
			if ( ! checkOutcomes()) {
				logger.warn("Previous run not finished; I'll wait, thanks");
				return multithreadOutcomes;
			}
		}
		
		List<String> agencies = prefetcher.agencyCodes();
		
		cleanCache();
		MDC.put("prefetch", "multi");
		
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
					} catch (Exception e) {
						logger.error("Problem running prefetcher for " + agency, e);
						throw e;
					} finally {
						MDC.clear();
					}
				}
				
			};
			
			multithreadOutcomes.put(agency,sked.submit(task));
		}
		
		logger.info("Launched {} tasks", multithreadOutcomes.size());
		if (logger.isDebugEnabled()) {
			checkOutcomes();
		}
		
		return multithreadOutcomes;
	}

	/**
	 * 
	 * @return true if all previous tasks are done
	 */
	public boolean checkOutcomes() {
		boolean allDone = true;
		// Have to get a bit fancy to avoid problems with modifying the map while iterating over it
		Iterator<Map.Entry<String,Future<PrefetchOutcome>>> it = multithreadOutcomes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,Future<PrefetchOutcome>> me = it.next();
			Future<PrefetchOutcome> f = me.getValue();
			String agency = me.getKey();
			boolean done = f.isDone();
			logger.info("future {} for {} done: {}", new Object[] {f, agency, done});
			if (done) {
				try {
					PrefetchOutcome outcome = f.get();
					logger.info("outcome for {}: {}", agency, outcome);
				} catch (Exception x) {
					logger.warn("got exception instead of outcome for " + agency, x);
				}
				it.remove();
			} else {
				allDone = false;
			}
		}
		return allDone;
	}
	
	// runs periodically.
	/** Update the rank statistics for one waterlevel sample set (chosen at random).
	 * 
	 */
	public void gatherWaterlevelRankStats() {
		wlsWorker.updateOne();
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

	public WaterlevelRankStatsWorker getWaterlevelRankStatsWorker() {
		return wlsWorker;
	}

	public void setWaterlevelRankStatsWorker(WaterlevelRankStatsWorker wlsWorker) {
		this.wlsWorker = wlsWorker;
	}
	
}

