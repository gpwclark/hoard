package com.uofantarctica.hoard;

import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Dequeue;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import com.uofantarctica.hoard.data_management.Hoard;
import com.uofantarctica.hoard.network_management.ExponentialInterestBackoff;
import com.uofantarctica.hoard.network_management.InitPrefix;
import com.uofantarctica.hoard.data_management.MemoryContentCache;
import com.uofantarctica.hoard.message_passing.BlockingQueue;
import com.uofantarctica.hoard.network_management.FaceInit;
import com.uofantarctica.hoard.network_management.LocalFace;
import com.uofantarctica.hoard.network_management.NdnServer;
import com.uofantarctica.hoard.message_passing.NonBlockingQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Main {
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	public static void main(String[] args) {
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.TRACE);
		//BasicConfigurator.configure();
		log.debug("Starting.");
		NonBlockingQueue<NdnEvent> ndnEvents = new NonBlockingQueue<>();
		BlockingQueue<NdnTraffic> ndnTraffic = new BlockingQueue<>(5l);
        Enqueue<NdnEvent> enQNdnEvent = new Enqueue(ndnEvents);
		Dequeue<NdnEvent> deQNdnEvent = new Dequeue(ndnEvents);

		Enqueue<NdnTraffic> enQNdnTraffic = new Enqueue(ndnTraffic);
		Dequeue<NdnTraffic> deQNdnTraffic = new Dequeue(ndnTraffic);
		ExecutorService dataHoardExecutor = Executors.newFixedThreadPool(1,
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable runnable) {
						Thread t = new Thread(runnable);
						t.setDaemon(true);
						t.setName("Hoard");
						return t;
					}
				});

		ExecutorService ndnExecutor = Executors.newFixedThreadPool(1,
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable runnable) {
						Thread t = new Thread(runnable);
						t.setDaemon(true);
						t.setName("NdnServer");
						return t;
					}
				});
		LocalFace face = FaceInit.getFace();
		NdnServer network = new NdnServer(face, deQNdnEvent);
		ndnExecutor.execute(network);


		MemoryContentCache cache = new MemoryContentCache(enQNdnEvent, enQNdnTraffic);
		Hoard hoard = new Hoard(cache, deQNdnTraffic);
		dataHoardExecutor.execute(hoard);

		List<InitPrefix> routesToMonitor = new ArrayList<>();
		/*
		routesToMonitor.add("/ndn/broadcast/ChronoChat-0.3");
		routesToMonitor.add("/ndn/broadcast/data");
		routesToMonitor.add("/ndn/broadcast/keys");
		routesToMonitor.add("/ndn/broadcast/atak");
		*/
		//routesToMonitor.add("/ndn/broadcast/");
		//routesToMonitor.add("/ndn/broadcast/");
		//routesToMonitor.add("/ndn/broadcast/edu/ucla/remap/ndnchat/");
		String routeName = "/ndn/broadcast/ChronoChat-0.3/ndnchat";
		routesToMonitor.add(new InitPrefix(routeName,
				InitPrefix.PrefixType.DSYNC,
                enQNdnEvent,
				enQNdnTraffic,
				cache));

		ExponentialInterestBackoff retryPolicy =
				new ExponentialInterestBackoff(5000, 120000, 30);

		for (InitPrefix r : routesToMonitor) {
			r.init( retryPolicy);
		}
		try {
			ndnExecutor.shutdown();
            while (!ndnExecutor.awaitTermination(24L, TimeUnit.HOURS)) {
                log.debug("Not yet. Still waiting for termination");
            }
		}
		catch (Exception e) {
		    log.error("Error while awaiting termination", e);
		}
	}
}
