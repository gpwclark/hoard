package com.uofantarctica.hoard.message_passing.traffic;

import com.uofantarctica.hoard.data_management.CacheOnInterestListener;
import com.uofantarctica.hoard.data_management.HoardServer;
import com.uofantarctica.hoard.message_passing.event.SimpleExpressInterest;
import com.uofantarctica.hoard.network_management.ExponentialBackoff;
import com.uofantarctica.hoard.protocols.HoardPrefixType;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Enqueue;
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

    private final String routeName;
    private final HoardPrefixType.PrefixType.ActionType type;
    private Enqueue<NdnEvent> enQNdnEvent;
    private Enqueue<NdnTraffic> enQNdnTraffic;
    private MemoryContentCache cache;
    private ExponentialBackoff retryPolicy;

	@Override
	public void process(HoardServer hoardServer) {
		enQNdnEvent = hoardServer.getEnQNdnEvent();
		enQNdnTraffic = hoardServer.getEnQNdnTraffic();
		cache = hoardServer.getCache();
		retryPolicy = hoardServer.getRetryPolicy(type);
		evaluate();
	}


    public InitPrefixTraffic(HoardPrefixType.PrefixType prefixType) {
        this.routeName = prefixType.getName();
        this.type = prefixType.getType();
    }

    public void evaluate() {
        switch (type) {
            case DSYNC: initDSyncPrefix();
                break;
	        case CHRONOSYNC: initChronoSyncPrefix();
				break;
            case REREQUEST: initFlatPrefix();
                break;
	        case CACHE: initNormalPrefix();
		        break;
	        case HOARD_DISCOVERY: initHoardDiscovery();
		        break;
            default: log.error("Got register prefix request with unknown PrefixType");
                break;
        }
    }

	private void initHoardDiscovery() {
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
    	ExponentialBackoff syncDataRetryPolicy =
			    new ExponentialBackoff(1000, 120000, -1);
		Name prefix = new Name(routeName);
		SyncDataHoarder hoarder = new SyncDataHoarder(enQNdnEvent, enQNdnTraffic, retryPolicy, cache);
    	/*
		SyncInterestListener interestListener = new SyncInterestListener(cache, enQNdnEvent, hoarder);
		enQNdnEvent.enQ(new RegisterPrefix(prefix, interestListener));
		*/

		//we have to ask someone for a rolodex that does not match theirs to get data back in the first place.
		Name rolodexName = new Name(prefix)
				.append(Long.toString(0L));
		Interest rolodexInterest = new Interest(rolodexName)
			.setInterestLifetimeMilliseconds(10000D);
		enQNdnEvent.enQ(new SimpleExpressInterest(rolodexInterest,
				new DSyncRolodexDataHoarder(routeName, hoarder, syncDataRetryPolicy)));

		// need to add the dsync prefix to the cache for hoard discovery.
	}

    private void initChronoSyncPrefix() {
        SyncDataHoarder hoarder = new SyncDataHoarder(enQNdnEvent, enQNdnTraffic,retryPolicy, cache);
        SyncInterestListener interestListener = new SyncInterestListener(cache, enQNdnEvent, hoarder);
        enQNdnEvent.enQ(new RegisterPrefix(new Name(routeName), interestListener));
    }
}
