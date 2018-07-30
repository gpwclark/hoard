package com.uofantarctica.hoard.data_management;

import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.message_passing.traffic.FlatDataTraffic;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import com.uofantarctica.hoard.network_management.ExponentialInterestBackoff;

public class FlatDataHoarder extends DataHoarder {
    private static final Logger log = LoggerFactory.getLogger(FlatDataHoarder.class);

    public FlatDataHoarder(Enqueue<NdnEvent> ndnEvents,
                           Enqueue<NdnTraffic> ndnTraffic,
                           ExponentialInterestBackoff retryPolicy) {
        super(ndnEvents, ndnTraffic, retryPolicy);
    }

    @Override
    protected void processData(Interest interest, Data data) {
        log.debug("Dequeue and enQ data: {} ", data.getName().toUri());
        ndnTraffic.enQ(new FlatDataTraffic(interest, data));
    }
}
