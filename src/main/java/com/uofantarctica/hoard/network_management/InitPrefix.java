package com.uofantarctica.hoard.network_management;

import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.ExpressInterest;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.message_passing.event.RegisterPrefix;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import com.uofantarctica.hoard.data_management.DSyncRolodexDataHoarder;
import com.uofantarctica.hoard.data_management.FlatDataHoarder;
import com.uofantarctica.hoard.data_management.FlatInterestListener;
import com.uofantarctica.hoard.data_management.MemoryContentCache;
import com.uofantarctica.hoard.data_management.SyncDataHoarder;
import com.uofantarctica.hoard.data_management.SyncInterestListener;

public class InitPrefix {
    private static final Logger log = LoggerFactory.getLogger(InitPrefix.class);
    public enum PrefixType {
        DSYNC,
	    CHRONOSYNC,
        FLAT
    }
    private final String routeName;
    private final PrefixType type;
    private final Enqueue<NdnEvent> enQNdnEvent;
    private final Enqueue<NdnTraffic> enQNdnTraffic;
    private final MemoryContentCache cache;

    public InitPrefix(String routeName, PrefixType type, Enqueue<NdnEvent> enQNdnEvent, Enqueue<NdnTraffic> enQNdnTraffic,
                      MemoryContentCache cache) {
        this.routeName = routeName;
        this.type = type;
        this.enQNdnEvent = enQNdnEvent;
        this.enQNdnTraffic = enQNdnTraffic;
        this.cache = cache;
    }

    public void init(ExponentialInterestBackoff retryPolicy) {
        switch (type) {
            case DSYNC: initDSyncPrefix(retryPolicy);
                break;
			case CHRONOSYNC: initChronoSyncPrefix(retryPolicy);
				break;
            case FLAT: initFlatPrefix(retryPolicy);
                break;
            default: log.error("Got register prefix request with unknown PrefixType");
                break;
        }
    }

    private void initFlatPrefix(ExponentialInterestBackoff retryPolicy) {
        FlatDataHoarder hoarder = new FlatDataHoarder(enQNdnEvent, enQNdnTraffic,retryPolicy);
        FlatInterestListener interestListener = new FlatInterestListener(cache, enQNdnEvent, hoarder);
        cache.registerFlatDataPrefix(new Name(routeName), interestListener);
    }

	private void initDSyncPrefix(ExponentialInterestBackoff retryPolicy) {
		Name prefix = new Name(routeName);
		SyncDataHoarder hoarder = new SyncDataHoarder(enQNdnEvent, enQNdnTraffic,retryPolicy);
    	/*
		SyncInterestListener interestListener = new SyncInterestListener(cache, enQNdnEvent, hoarder);
		enQNdnEvent.enQ(new RegisterPrefix(prefix, interestListener));
		*/

		//we have to ask someone for a rolodex that does not match theirs to get data back in the first place.
		Name rolodexName = new Name(prefix)
				.append(Long.toString(0L));
		Interest rolodexInterest = new Interest(rolodexName)
			.setInterestLifetimeMilliseconds(10000D);
		enQNdnEvent.enQ(new ExpressInterest(rolodexInterest,
				new DSyncRolodexDataHoarder(routeName, hoarder, retryPolicy.duplicate())));
	}

    private void initChronoSyncPrefix(ExponentialInterestBackoff retryPolicy) {
        SyncDataHoarder hoarder = new SyncDataHoarder(enQNdnEvent, enQNdnTraffic,retryPolicy);
        SyncInterestListener interestListener = new SyncInterestListener(cache, enQNdnEvent, hoarder);
        enQNdnEvent.enQ(new RegisterPrefix(new Name(routeName), interestListener));
    }
}
