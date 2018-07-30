package com.uofantarctica.hoard.data_management;

import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.network_management.InterestListener;

public class FlatInterestListener extends InterestListener {

    public FlatInterestListener(com.uofantarctica.hoard.data_management.MemoryContentCache cache, Enqueue<NdnEvent> ndnEvents, com.uofantarctica.hoard.data_management.FlatDataHoarder hoarder) {
        super(cache, ndnEvents, hoarder);
    }

    @Override
    public void processInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
    	//TODO can we steal something from the content cache, e.g. pending interests to keep from sending repeats?
	    super.sendInitialInterest(interest);
        //TODO we maaay not want to store interests with exclude filters... or we may want to just keep them in the
        // cache for less time.
        cache.storePendingInterest(interest);
    }
}
