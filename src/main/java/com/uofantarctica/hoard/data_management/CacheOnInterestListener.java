package com.uofantarctica.hoard.data_management;

import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.network_management.InterestListener;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;

public class CacheOnInterestListener extends InterestListener {
	public CacheOnInterestListener(MemoryContentCache cache, Enqueue<NdnEvent> ndnEvents, DataHoarder hoarder) {
		super(cache, ndnEvents, hoarder);
	}

	@Override
	protected void processInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
		cache.storePendingInterest(interest);
	}
}
