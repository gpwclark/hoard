package com.uofantarctica.hoard.protocols;

import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import com.uofantarctica.hoard.message_passing.event.ExpressInterest;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.data_management.SyncDataHoarder;

public class ChronoSyncPacket extends SyncStateProtoPacket {
    public ChronoSyncPacket(com.uofantarctica.hoard.protocols.SyncStateProto.SyncStateMsg syncStateMsg) {
        super(syncStateMsg);
    }

    @Override
    public NdnEvent makeEvent(Name n, SyncDataHoarder hoarder) {
    	//Since these are items in a chronosync packet we just need a flat data hoarder
	    // as this is simple retrieval.
        return new ExpressInterest(new Interest(n), hoarder.newFlatDataHoarder());
    }

	@Override
	public Name makeName(com.uofantarctica.hoard.protocols.SyncStateProto.SyncState s) {
    	Name n = new Name(s.getName())
			    .append(Long.toString(s.getSeqno().getSession()))
			    .append(Long.toString(s.getSeqno().getSeq()));
		return n;
	}

	@Override
	public Name getUniqueName(com.uofantarctica.hoard.protocols.SyncStateProto.SyncState s) {
    	Name n = new Name(s.getName())
			    .append(Long.toString(s.getSeqno().getSession()));
    	return n;
	}
}
