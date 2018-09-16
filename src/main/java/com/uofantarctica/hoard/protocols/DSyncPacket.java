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

	//TODO do we need all of these arguments to InitPrefixTraffic?
	@Override
	public NdnTraffic makeInitPrefixTraffic(SyncStateProto.SyncState s, SyncDataHoarder hoarder) {
		Name n = getUniqueName(s);
		HoardPrefixType.PrefixType.Builder builder = HoardPrefixType.PrefixType.newBuilder();
		String routeName = n.toUri();
		builder.setName(routeName)
				.setType(HoardPrefixType.PrefixType.ActionType.CACHE);
		HoardPrefixType.PrefixType prefixType = builder.build();
		return new InitPrefixTraffic(prefixType);
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
