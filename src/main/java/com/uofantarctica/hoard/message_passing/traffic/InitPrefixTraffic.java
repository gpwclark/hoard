package com.uofantarctica.hoard.message_passing.traffic;

import com.uofantarctica.hoard.data_management.CacheOnInterestListener;
import com.uofantarctica.hoard.data_management.Hoard;
import com.uofantarctica.hoard.network_management.ExponentialInterestBackoff;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.ExpressInterest;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.message_passing.event.RegisterPrefix;
import com.uofantarctica.hoard.data_management.DSyncRolodexDataHoarder;
import com.uofantarctica.hoard.data_management.FlatDataHoarder;
import com.uofantarctica.hoard.data_management.ParrotOnInterestListener;
import com.uofantarctica.hoard.data_management.MemoryContentCache;
import com.uofantarctica.hoard.data_management.SyncDataHoarder;
import com.uofantarctica.hoard.data_management.SyncInterestListener;

public class InitPrefixTraffic implements NdnTraffic {
    private static final Logger log = LoggerFactory.getLogger(InitPrefixTraffic.class);

	@Override
	public void process(Hoard hoard) {
		init();
	}

	public enum PrefixType {
        DSYNC,
	    CHRONOSYNC,
		FLAT_CACHE_AND_PARROT_ON_INTEREST,
		FLAT_CACHE_ON_INTEREST
    }
    private final String routeName;
    private final PrefixType type;
    private final Enqueue<NdnEvent> enQNdnEvent;
    private final Enqueue<NdnTraffic> enQNdnTraffic;
    private final MemoryContentCache cache;
    private final ExponentialInterestBackoff retryPolicy;

    public InitPrefixTraffic(String routeName, PrefixType type, Enqueue<NdnEvent> enQNdnEvent, Enqueue<NdnTraffic> enQNdnTraffic,
                             MemoryContentCache cache, ExponentialInterestBackoff retryPolicy) {
        this.routeName = routeName;
        this.type = type;
        this.enQNdnEvent = enQNdnEvent;
        this.enQNdnTraffic = enQNdnTraffic;
        this.cache = cache;
        this.retryPolicy = retryPolicy;
    }

    public void init() {
        switch (type) {
            case DSYNC: initDSyncPrefix();
                break;
			case CHRONOSYNC: initChronoSyncPrefix();
				break;
            case FLAT_CACHE_AND_PARROT_ON_INTEREST: initFlatPrefix();
                break;
	        case FLAT_CACHE_ON_INTEREST: initNormalPrefix();
		        break;
            default: log.error("Got register prefix request with unknown PrefixType");
                break;
        }
    }

	private void initNormalPrefix() {
		FlatDataHoarder hoarder = new FlatDataHoarder(enQNdnEvent, enQNdnTraffic,retryPolicy);
		CacheOnInterestListener interestListener = new CacheOnInterestListener(cache, enQNdnEvent, hoarder);
		cache.registerFlatDataPrefix(new Name(routeName), interestListener);
	}

	private void initFlatPrefix() {
        FlatDataHoarder hoarder = new FlatDataHoarder(enQNdnEvent, enQNdnTraffic,retryPolicy);
        ParrotOnInterestListener interestListener = new ParrotOnInterestListener(cache, enQNdnEvent, hoarder);
        cache.registerFlatDataPrefix(new Name(routeName), interestListener);
    }

	private void initDSyncPrefix() {
		Name prefix = new Name(routeName);
		SyncDataHoarder hoarder = new SyncDataHoarder(enQNdnEvent, enQNdnTraffic,retryPolicy, cache);
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

    private void initChronoSyncPrefix() {
        SyncDataHoarder hoarder = new SyncDataHoarder(enQNdnEvent, enQNdnTraffic,retryPolicy, cache);
        SyncInterestListener interestListener = new SyncInterestListener(cache, enQNdnEvent, hoarder);
        enQNdnEvent.enQ(new RegisterPrefix(new Name(routeName), interestListener));
    }
}
