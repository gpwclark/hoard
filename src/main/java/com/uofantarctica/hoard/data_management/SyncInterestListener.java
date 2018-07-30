package com.uofantarctica.hoard.data_management;

import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.network_management.InterestListener;

public class SyncInterestListener extends InterestListener {
	private static final Logger log = LoggerFactory.getLogger(SyncInterestListener.class);

	public SyncInterestListener(com.uofantarctica.hoard.data_management.MemoryContentCache cache,
	                            Enqueue<NdnEvent> ndnEvents,
	                            com.uofantarctica.hoard.data_management.SyncDataHoarder hoarder) {
		super(cache, ndnEvents, hoarder);
	}

	@Override
	protected void processInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
		//TODO can we steal something from the content cache, e.g. pending interests to keep from sending repeats?
		super.sendInitialInterest(interest);
	}
}
