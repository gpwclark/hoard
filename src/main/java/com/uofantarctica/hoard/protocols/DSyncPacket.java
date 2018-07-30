package com.uofantarctica.hoard.protocols;

import com.uofantarctica.dsync.model.ReturnStrategy;
import net.named_data.jndn.Name;
import com.uofantarctica.hoard.data_management.SyncDataHoarder;
import com.uofantarctica.hoard.message_passing.event.ExpressIncrementingInterest;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;

public class DSyncPacket extends SyncStateProtoPacket {
    public DSyncPacket(SyncStateProto.SyncStateMsg syncStateMsg) {
        super(syncStateMsg);
    }

    @Override
    public NdnEvent makeEvent(net.named_data.jndn.Name n, SyncDataHoarder hoarder) {
    	// since this is a dsync packet we start fetching at 0 and continue ad infinitum.
	    // since we are starting to fetch this producer's data we must start at the 0th bit.
	    return new ExpressIncrementingInterest(n, hoarder.newIncrementingDataHoarder(hoarder, n));
    }

	@Override
	public Name makeName(SyncStateProto.SyncState s) {
		Name n = new Name(s.getName())
				.append(ReturnStrategy.EXACT.toString())
				.append(Long.toString(s.getSeqno().getSession()))
				.append(Long.toString(0L));
		return n;
	}

	@Override
	public Name getUniqueName(SyncStateProto.SyncState s) {
    	Name n = new Name(s.getName())
			    .append(Long.toString(s.getSeqno().getSession()));
		return n;
	}
}
