package com.uofantarctica.hoard.protocols;

import com.uofantarctica.hoard.message_passing.event.SimpleExpressInterest;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.data_management.SyncDataHoarder;

public class ChronoSyncPacket extends SyncStateProtoPacket {
    public ChronoSyncPacket(com.uofantarctica.jndn.proto.SyncStateProto.SyncStateMsg syncStateMsg) {
        super(syncStateMsg);
    }

	private Name makeExpressInterestName(com.uofantarctica.jndn.proto.SyncStateProto.SyncState s) {
    	Name n = new Name(s.getName())
			    .append(Long.toString(s.getSeqno().getSession()))
			    .append(Long.toString(s.getSeqno().getSeq()));
		return n;
	}

	@Override
	public NdnEvent makeExpressInterestEvent(com.uofantarctica.jndn.proto.SyncStateProto.SyncState s, SyncDataHoarder
			hoarder) {
    	Name n = makeExpressInterestName(s);
    	//TODO what's the timeout here?
		return new SimpleExpressInterest(new Interest(n), hoarder.newFlatDataHoarder());
	}

	@Override
	public Name getUniqueName(com.uofantarctica.jndn.proto.SyncStateProto.SyncState s) {
    	Name n = new Name(s.getName())
			    .append(Long.toString(s.getSeqno().getSession()));
    	return n;
	}

	@Override
	public NdnTraffic makeInitPrefixTraffic(com.uofantarctica.jndn.proto.SyncStateProto.SyncState s, SyncDataHoarder
			hoarder) {
		return null;
	}

	/*
	@Override
	public Name makeInitPrefixTraffic(com.uofantarctica.jndn.proto.SyncStateProto.SyncState s) {
		return new Name(s.getName())
				.append(Long.toString(s.getSeqno().getSession()));
	}
	*/
}
