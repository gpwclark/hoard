package com.uofantarctica.hoard.protocols;

import com.uofantarctica.dsync.model.ReturnStrategy;
import com.uofantarctica.hoard.message_passing.traffic.InitPrefixTraffic;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import net.named_data.jndn.Name;
import com.uofantarctica.hoard.data_management.SyncDataHoarder;
import com.uofantarctica.hoard.message_passing.event.ExpressIncrementingInterest;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;

public class DSyncPacket extends SyncStateProtoPacket {
    public DSyncPacket(SyncStateProto.SyncStateMsg syncStateMsg) {
        super(syncStateMsg);
    }

	public Name makeExpressInterestName(SyncStateProto.SyncState s) {
		Name n = new Name(s.getName())
				.append(ReturnStrategy.EXACT.toString())
				.append(Long.toString(s.getSeqno().getSession()))
				// since this is a dsync packet we start fetching at 0 and continue ad infinitum.
				// since we are starting to fetch this producer's data we must start at the 0th bit.
				.append(Long.toString(0L));
		return n;
	}

	@Override
	public NdnEvent makeExpressInterestEvent(SyncStateProto.SyncState s, SyncDataHoarder hoarder) {
    	Name n = makeExpressInterestName(s);
    	return new ExpressIncrementingInterest(n, hoarder.newIncrementingDataHoarder(hoarder, n));
	}

	@Override
	public Name getUniqueName(SyncStateProto.SyncState s) {
    	Name n = new Name(s.getName())
			    .append(ReturnStrategy.EXACT.toString())
			    .append(Long.toString(s.getSeqno().getSession()));
		return n;
	}

	@Override
	public NdnTraffic makeInitPrefixTraffic(SyncStateProto.SyncState s, SyncDataHoarder hoarder) {
    	Name n = getUniqueName(s);
		return new InitPrefixTraffic(n.toString(),
				InitPrefixTraffic.PrefixType.FLAT_CACHE_ON_INTEREST,
				hoarder.getEnQNdnEvent(),
				hoarder.getEnQNdnTraffic(),
				hoarder.getCache(),
				hoarder.getRetryPolicy());

	}

	/*
	@Override
	public Name makeInitPrefixTraffic(SyncStateProto.SyncState s) {
		Name n = new Name(s.getName())
				.append(ReturnStrategy.EXACT.toString())
				.append(Long.toString(s.getSeqno().getSession()));
		return n;
	}
	*/
}
