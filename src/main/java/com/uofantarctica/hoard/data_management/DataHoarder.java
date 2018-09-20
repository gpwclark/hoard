package com.uofantarctica.hoard.data_management;

import com.uofantarctica.hoard.message_passing.DelayedNdnEvent;
import com.uofantarctica.hoard.message_passing.event.ExpressInterest;
import com.uofantarctica.hoard.message_passing.event.ExpressSingleInterest;
import com.uofantarctica.hoard.message_passing.event.SimpleExpressInterest;
import com.uofantarctica.hoard.network_management.ExponentialBackoff;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.NetworkNack;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnNetworkNack;
import net.named_data.jndn.OnTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;

public abstract class DataHoarder implements OnData, OnTimeout, OnNetworkNack {
    private static final Logger log = LoggerFactory.getLogger(DataHoarder.class);

    protected final Enqueue<NdnEvent> enQNdnEvents;
	protected final Enqueue<NdnTraffic> ndnTraffic;
	protected final ExponentialBackoff retryPolicy;
    public DataHoarder(Enqueue<NdnEvent> enQNdnEvents,
                       Enqueue<NdnTraffic> ndnTraffic,
                       ExponentialBackoff retryPolicy) {
        this.enQNdnEvents = enQNdnEvents;
	    this.ndnTraffic = ndnTraffic;
	    this.retryPolicy = retryPolicy;
	    retryPolicy.begin();
    }

	@Override
    public void onData(Interest interest, Data data) {
    	retryPolicy.begin(); //need to make sure we're not ever carrying forward retries.
        processData(interest, data);
    }

    protected abstract void processData(Interest interest, Data data);

    //TODO keep track of which interets are expressed
    @Override
    public void onNetworkNack(Interest interest, NetworkNack networkNack) {
    	retryPolicy.incAttempts();
    	//TODO would be possible to make this delay more accurate, i.e. the delay should
	    // exclude the amount of time it took the back to come back, and in some cases,
	    // there may be little or no delay.
			long delay = retryPolicy.getInterestLifetime();
			NdnEvent event = new ExpressSingleInterest(interest, this);
			DelayedNdnEvent delayedNdnEvent = new DelayedNdnEvent(delay, event);
			enQNdnEvents.enQ(delayedNdnEvent);
    }

	@Override
    public void onTimeout(Interest interest) {
	    retryPolicy.incAttempts();
	    if (retryPolicy.allowRetryNonBlocking()) {
			retry(interest);
	    }
	    else {
		    maxedOutRetries(interest);
	    }
    }

    public void retry(Interest interest) {
		log.debug("Retrying expressInterest: {} ", interest.getName().toUri());
		expressInterest(interest, this);
    }

	protected void maxedOutRetries(Interest interest) {
		log.error("Failed max number of tries for given interest: {} ", interest.toUri());
	}

	public void expressInterest(Interest newInterest, DataHoarder hoarder) {
        try {
	        newInterest.setInterestLifetimeMilliseconds(retryPolicy.getInterestLifetime());
            enQNdnEvents.enQ(new SimpleExpressInterest(newInterest, hoarder));
        } catch (Exception e) {
            log.error("Failed to retry and express interest", e);
        }
    }

	public com.uofantarctica.hoard.data_management.FlatDataHoarder newFlatDataHoarder() {
		return new com.uofantarctica.hoard.data_management.FlatDataHoarder(enQNdnEvents, ndnTraffic, retryPolicy.duplicate());
	}

	public com.uofantarctica.hoard.data_management.IncrementingDataHoarder newIncrementingDataHoarder(com.uofantarctica.hoard.data_management.SyncDataHoarder syncDataHoarder, Name prefix) {
    	return new com.uofantarctica.hoard.data_management.IncrementingDataHoarder(enQNdnEvents, ndnTraffic, retryPolicy.duplicate(), syncDataHoarder, prefix);
	}
}
