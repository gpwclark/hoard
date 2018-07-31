package com.uofantarctica.hoard.data_management;

import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.ExpressInterest;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.message_passing.traffic.FlatDataTraffic;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import com.uofantarctica.hoard.network_management.ExponentialInterestBackoff;

public class IncrementingDataHoarder extends DataHoarder {
	private static final Logger log = LoggerFactory.getLogger(IncrementingDataHoarder.class);

	private final com.uofantarctica.hoard.data_management.SyncDataHoarder syncDataHoarder;
	private final Name dsyncProducerPrefix;
	private long currentSegment = 0L;

	public IncrementingDataHoarder(Enqueue<NdnEvent> ndnEvents, Enqueue<NdnTraffic> ndnTraffic,
	                               ExponentialInterestBackoff retryPolicy,
	                               com.uofantarctica.hoard.data_management.SyncDataHoarder syncDataHoarder, Name initialPrefix) {
		super(ndnEvents, ndnTraffic, retryPolicy);
		this.syncDataHoarder = syncDataHoarder;
		this.dsyncProducerPrefix = initialPrefix.getSubName(0, initialPrefix.size() - 1);
	}

	@Override
	protected void processData(Interest interest, Data data) {
		log.debug("Dequeue and enQ data: {} ", data.getName().toUri());
		ndnTraffic.enQ(new FlatDataTraffic(interest, data));
		++currentSegment;
		Name nextDataName = new Name(dsyncProducerPrefix)
				.append(Long.toString(currentSegment));
		ndnEvents.enQ(new ExpressInterest(new Interest(nextDataName), this));
	}

	@Override
	public void maxedOutRetries(Interest interest) {
		super.maxedOutRetries(interest);
		syncDataHoarder.removeSyncName(dsyncProducerPrefix);
	}
}
