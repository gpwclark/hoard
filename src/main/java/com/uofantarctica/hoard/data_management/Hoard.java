package com.uofantarctica.hoard.data_management;

import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Dequeue;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;

public class Hoard implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(Hoard.class);

	private final MemoryContentCache cache;
	private final Dequeue<NdnTraffic> ndnTraffic;

	public Hoard(MemoryContentCache cache,
				 Dequeue<NdnTraffic> ndnTraffic) {
		this.cache = cache;
		this.ndnTraffic = ndnTraffic;
	}

	@Override
	public void run() {
		while (true) {
		    try {
                NdnTraffic newNdnTraffic = ndnTraffic.deQ();
                while (newNdnTraffic != null) {
                    log.debug("Processing ndnTraffic: {} ", newNdnTraffic);
                    newNdnTraffic.process(this);
					newNdnTraffic = ndnTraffic.deQ();
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
}
