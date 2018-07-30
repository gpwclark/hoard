package com.uofantarctica.hoard.network_management;

import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnInterestCallback;
import net.named_data.jndn.OnRegisterFailed;
import net.named_data.jndn.OnRegisterSuccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.ExpressInterest;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.data_management.DataHoarder;
import com.uofantarctica.hoard.data_management.MemoryContentCache;

public abstract class InterestListener implements OnInterestCallback, OnRegisterFailed, OnRegisterSuccess {
    private static final Logger log = LoggerFactory.getLogger(InterestListener.class);

    protected final MemoryContentCache cache;
    protected final Enqueue<NdnEvent> ndnEvents;
    protected final DataHoarder hoarder;

    public InterestListener(MemoryContentCache cache, Enqueue<NdnEvent> ndnEvents, DataHoarder hoarder) {
        this.cache = cache;
        this.ndnEvents = ndnEvents;
        this.hoarder = hoarder;
    }

    @Override
    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        processInterest(prefix, interest, face, interestFilterId, filter);
    }

    protected abstract void processInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter);

    protected void sendInitialInterest(Interest interest) {
    	interest.setInterestLifetimeMilliseconds(interest.getInterestLifetimeMilliseconds());
    	ndnEvents.enQ(new ExpressInterest(interest, hoarder));
    }

    @Override
    public void onRegisterFailed(Name prefix) {
        cache.registerFlatDataPrefix(prefix, this);
    }

    @Override
    public void onRegisterSuccess(Name prefix, long registeredPrefixId) {
        log.debug("Successfully registered prefix: {} ", prefix.toUri());

    }
}
