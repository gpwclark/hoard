package com.uofantarctica.hoard;

import com.uofantarctica.dsync.model.ReturnStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.uofantarctica.dsync.DSync;
import com.uofantarctica.hoard.data_management.HoardServer;
import com.uofantarctica.hoard.protocols.HoardPrefixType;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.OnData;
import net.named_data.jndn.sync.ChronoSync2013;
import net.named_data.jndn.util.Blob;
import com.uofantarctica.hoard.message_passing.Dequeue;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import com.uofantarctica.hoard.message_passing.traffic.InitPrefixTraffic;
import com.uofantarctica.hoard.data_management.MemoryContentCache;
import com.uofantarctica.hoard.message_passing.BlockingQueue;
import com.uofantarctica.hoard.network_management.FaceInit;
import com.uofantarctica.hoard.network_management.LocalFace;
import com.uofantarctica.hoard.network_management.NdnServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Hoard implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(Hoard.class);

	private final String theDataPrefix;
	private final String theBroadcastPrefix;
	private final String chatRoom;
	private final String screenName;
	private DSync dsync;
	private BlockingQueue<NdnEvent> ndnEvents;
	private BlockingQueue<NdnTraffic> ndnTraffic;
	private Enqueue<NdnEvent> enQNdnEvent;
	private Dequeue<NdnEvent> deQNdnEvent;
	private Enqueue<NdnTraffic> enQNdnTraffic;
	private Dequeue<NdnTraffic> deQNdnTraffic;
	MemoryContentCache cache;
	private AtomicBoolean running = new AtomicBoolean(false);

	public Hoard(String theDataPrefix, String theBroadcastPrefix, String chatRoom, String screenName) {
		this.theDataPrefix = theDataPrefix;
		this.theBroadcastPrefix = theBroadcastPrefix;
		this.chatRoom = chatRoom;
		this.screenName = screenName;
	}

	private void initQueues() {
		ndnEvents = new BlockingQueue<>(10L, TimeUnit.MILLISECONDS);
		ndnTraffic = new BlockingQueue<>(5L, TimeUnit.SECONDS);
		enQNdnEvent = new Enqueue(ndnEvents);
		deQNdnEvent = new Dequeue(ndnEvents);
		enQNdnTraffic = new Enqueue(ndnTraffic);
		deQNdnTraffic = new Dequeue(ndnTraffic);
	}

	private void init() {
		//TODO there is NO reason to have TWO executor services.
		ExecutorService dataHoardExecutor = Executors.newFixedThreadPool(1,
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable runnable) {
						Thread t = new Thread(runnable);
						t.setDaemon(true);
						t.setName("HoardServer");
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

		//TODO dsync needs to live in network server and the necessary ndnEvents
		// need to be able to route to the dsync.publish method.
		/*
		dsync = new DSync(
				new OnData() {
					@Override
					public void onData(Interest interest, Data data) {
						try {
							HoardPrefixType.PrefixType prefixType = HoardPrefixType.PrefixType.parseFrom(data.getContent().getImmutableArray());
							enQNdnTraffic.enQ(new InitPrefixTraffic(prefixType));
						} catch (InvalidProtocolBufferException e) {
							log.error("failed to decode hoardServer prefix type: {}", data.getName().toUri());
						}
					}
				},
				new ChronoSync2013.OnInitialized() {
					@Override
					public void onInitialized() {
						log.debug("HoardServer prefix discovery dsync route initialized.");
					}
				},
				theDataPrefix,
				theBroadcastPrefix,
				System.currentTimeMillis(),
				face.getFace(),
				FaceInit.getSecurityData(face.getFace()).keyChain,
				chatRoom,
				screenName,
				ReturnStrategy.EXACT);
				*/
		cache = new MemoryContentCache(enQNdnEvent, enQNdnTraffic);
		/* TODO in order to work out federation, caches are going to need to be
		 * separate by namespace, or at least tracked separately.
		 * Sync namespaces will always have there own cache. Given
		 * the nature (predictability in naming) of sync protocols, federation is taken
		 * care of, a sync data packet is all you need to start asking for the right data.
		 * For all other kinds of prefixes, there's no way to know what data to ask for,
		 * so, instances of hoardServer wishing to synchronize these kinds of prefixes must
		 * communicate the names of their non-sync data, so the other knows what to ask
		 * for. This means that hoardServer will use one dsync prefix internally for different
		 * federated behavior. The first dsync prefix, /hoardServer/prefix_types/. communicates
		 * the various calls to init prefix that instance of hoardServer has been given. Each one
		 * is it's own datum. Instances of hoardServer communicate this data to each other and
		 * decide whether or not to opt in to its collection. Once a given prefix has
		 * been selected, it's data collection begins. If it is not sync data, this
		 * requires an extra step. The prefix, /hoardServer/datasets/<name>/#, will be used for communicating
		 * the names of data in that dataset. Interested parties need only ask for
		 * numbered data of the datasets they're interested to  learn what to ask for.
		 */
		HoardServer hoardServer = new HoardServer(enQNdnEvent, enQNdnTraffic, cache, deQNdnTraffic);
		dataHoardExecutor.execute(hoardServer);

		while (running.get()) {
			try {
				Thread.sleep(5000L);
			}
			catch (Exception e) {
				log.error("Error while awaiting termination.", e);
			}
		}
		try {
			 ndnExecutor.shutdownNow();
			 dataHoardExecutor.shutdownNow();
			 /*
			 ndnExecutor.shutdown();
			 while (!ndnExecutor.awaitTermination(42L, TimeUnit.DAYS)) {
				 log.debug("Not yet. Still waiting for termination.");
			 }
			 */
		 }
		 catch (Exception e) {
			 log.error("Error while terminating hoard.", e);
		 }

		 log.debug("Hoard done running.");
	}

	public void addRoute(HoardPrefixType.PrefixType traffic) {
		//TODO if we do end up letting go of a prefix, do we properly handle removing it from the rolodex?
		// if so, and they were still in the rolodex, wouldn't we end up adding them back and rerequesting
		// all their data, needs to be thought through more.
		enQNdnTraffic.enQ(new InitPrefixTraffic(traffic));
		//TODO this should be an option for enQNdnEvents... not a direct call.
		//dsync.publishNextMessage(new Data().setContent(new Blob(traffic.toByteArray())));
	}

	public Optional<Data> testCache(Interest interest) {
		return cache.getData(interest);
	}

	public void interrupt() {
		running.set(false);
		log.debug("Hoard thread signalled to stop running.");
	}

	@Override
	public void run() {
		running.set(true);
		initQueues();
		//TODO init should not be the method that houses the main while loop.
		init();
	}
}
