package com.uofantarctica.hoard.data_management;

import com.uofantarctica.hoard.HoardThread;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.network_management.ExponentialBackoff;
import com.uofantarctica.hoard.protocols.HoardPrefixType;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Dequeue;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;

public class HoardServer implements Runnable, HoardThread {
	private static final Logger log = LoggerFactory.getLogger(HoardServer.class);

	private static final String HOARD_SERVER = "HOARD_SERVER";
	private final MemoryContentCache cache;
	private final Enqueue<NdnEvent> ndnEvents;
	private final Enqueue<NdnTraffic> ndnTrafficEnqueue;
	private final Dequeue<NdnTraffic> ndnTrafficDequeue;

	public HoardServer(Enqueue<NdnEvent> ndnEvents,
	                   Enqueue<NdnTraffic> ndnTrafficEnqueue,
	                   MemoryContentCache cache,
	                   Dequeue<NdnTraffic> ndnTrafficDequeue) {
		this.ndnEvents = ndnEvents;
		this.ndnTrafficEnqueue = ndnTrafficEnqueue;
		this.cache = cache;
		this.ndnTrafficDequeue = ndnTrafficDequeue;
	}

	@Override
	public void run() {
		while (true) {
		    try {
                NdnTraffic newNdnTraffic = ndnTrafficDequeue.deQ();
                while (newNdnTraffic != null) {
                    log.debug("Processing ndnTrafficDequeue: {} ", newNdnTraffic);
                    newNdnTraffic.process(this);
					newNdnTraffic = ndnTrafficDequeue.deQ();
                }
			}
			catch(Exception e) {
		    	log.error("Error in data hoard loop.", e);
			}
		}
	}

	public void processFlatInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
		cache.processInterest(prefix, interest, face, interestFilterId, filter);
	}

	public void processFlatData(Interest interest, Data data) {
		log.debug("Adding data with name: {}", data.getName().toUri());
		//hoard.put(data);
		cache.add(data);
	}

	public Enqueue<NdnEvent> getEnQNdnEvent() {
		return ndnEvents;
	}

	public Enqueue<NdnTraffic> getEnQNdnTraffic() {
		return ndnTrafficEnqueue;
	}

	public MemoryContentCache getCache() {
		return cache;
	}

	public ExponentialBackoff getRetryPolicy(HoardPrefixType.PrefixType.ActionType type) {
		ExponentialBackoff retryPolicy =
				new ExponentialBackoff(5000, 120000, -1);
		return retryPolicy;
	}

	@Override
	public String getName() {
		return HOARD_SERVER;
	}
}
