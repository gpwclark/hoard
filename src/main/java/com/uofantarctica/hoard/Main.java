package com.uofantarctica.hoard;

import com.google.protobuf.InvalidProtocolBufferException;
import com.uofantarctica.dsync.DSync;
import com.uofantarctica.dsync.model.ReturnStrategy;
import com.uofantarctica.hoard.network_management.ExponentialBackoff;
import com.uofantarctica.hoard.protocols.HoardPrefixType;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.OnData;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Dequeue;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import com.uofantarctica.hoard.data_management.Hoard;
import com.uofantarctica.hoard.message_passing.traffic.InitPrefixTraffic;
import com.uofantarctica.hoard.data_management.MemoryContentCache;
import com.uofantarctica.hoard.message_passing.BlockingQueue;
import com.uofantarctica.hoard.network_management.FaceInit;
import com.uofantarctica.hoard.network_management.LocalFace;
import com.uofantarctica.hoard.network_management.NdnServer;
import com.uofantarctica.hoard.message_passing.NonBlockingQueue;

import java.io.IOException;
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
		LocalFace face = null;
		try {
			face = FaceInit.getFace(enQNdnEvent);
		} catch (IOException e) {
			face = new LocalFace(enQNdnEvent);
		}
		NdnServer network = new NdnServer(face, deQNdnEvent);
		ndnExecutor.execute(network);

		DSync dsync = new DSync(
			new OnData() {
				@Override
				public void onData(Interest interest, Data data) {
					try {
						HoardPrefixType.PrefixType prefixType = HoardPrefixType.PrefixType.parseFrom(data.getContent().getImmutableArray());
						enQNdnTraffic.enQ(new InitPrefixTraffic(prefixType));
					} catch (InvalidProtocolBufferException e) {
						log.error("failed to decode hoard prefix type: {}", data.getName().toUri());
					}
				}
			},
			new ChronoSync2013.OnInitialized() {
				@Override
				public void onInitialized() {
					log.debug("Hoard prefix discovery dsync route initialized.");
				}
			},
			"/ndn/broadcast/data/hoard/",
			"/ndn/broadcast/hoard/prefix_types",
			System.currentTimeMillis(),
			face.getFace(),
			FaceInit.getSecurityData(face.getFace()).keyChain,
			"hoard-prefix-disocvery",
			"hoard",
			ReturnStrategy.EXACT);
		MemoryContentCache cache = new MemoryContentCache(enQNdnEvent, enQNdnTraffic);
		/* TODO in order to work out federation, caches are going to need to be
		 * separate by namespace, or at least tracked separately.
		 * Sync namespaces will always have there own cache. Given
		 * the nature (predictability in naming) of sync protocols, federation is taken
		 * care of, a sync data packet is all you need to start asking for the right data.
		 * For all other kinds of prefixes, there's no way to know what data to ask for,
		 * so, instances of hoard wishing to synchronize these kinds of prefixes must
		 * communicate the names of their non-sync data, so the other knows what to ask
		 * for. This means that hoard will use one dsync prefix internally for different
		 * federated behavior. The first dsync prefix, /hoard/prefix_types/. communicates
		 * the various calls to init prefix that instance of hoard has been given. Each one
		 * is it's own datum. Instances of hoard communicate this data to each other and
		 * decide whether or not to opt in to its collection. Once a given prefix has
		 * been selected, it's data collection begins. If it is not sync data, this
		 * requires an extra step. The prefix, /hoard/datasets/<name>/#, will be used for communicating
		 * the names of data in that dataset. Interested parties need only ask for
		 * numbered data of the datasets they're interested to  learn what to ask for.
		 */
		Hoard hoard = new Hoard(enQNdnEvent, enQNdnTraffic, cache, deQNdnTraffic);
		dataHoardExecutor.execute(hoard);

		List<InitPrefixTraffic> routesToMonitor = new ArrayList<>();
		/*
		routesToMonitor.add("/ndn/broadcast/ChronoChat-0.3");
		routesToMonitor.add("/ndn/broadcast/data");
		routesToMonitor.add("/ndn/broadcast/keys");
		routesToMonitor.add("/ndn/broadcast/atak");
		*/
		//routesToMonitor.add("/ndn/broadcast/");
		//routesToMonitor.add("/ndn/broadcast/");
		//routesToMonitor.add("/ndn/broadcast/edu/ucla/remap/ndnchat/");
		HoardPrefixType.PrefixType.Builder builder = HoardPrefixType.PrefixType.newBuilder();
		String routeName = "/ndn/broadcast/ChronoChat-0.3/ndnchat";
		builder.setName(routeName)
			.setType(HoardPrefixType.PrefixType.ActionType.DSYNC);
		HoardPrefixType.PrefixType prefixType = builder.build();
		dsync.publishNextMessage(new Data().setContent(new Blob(prefixType.toByteArray())));
		//TODO if we do end up letting go of a prefix, do we properly handle removing it from the rolodex?
		// if so, and they were still in the rolodex, wouldn't we end up adding them back and rerequesting
		// all their data, needs to be thought through more.
		routesToMonitor.add(new InitPrefixTraffic(prefixType));

		for (InitPrefixTraffic r : routesToMonitor) {
			enQNdnTraffic.enQ(r);
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
