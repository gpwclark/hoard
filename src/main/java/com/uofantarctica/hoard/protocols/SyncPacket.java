package com.uofantarctica.hoard.protocols;

import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import net.named_data.jndn.Name;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.data_management.SyncDataHoarder;

public interface SyncPacket {
    int getCount();
    com.uofantarctica.jndn.proto.SyncStateProto.SyncState get(int i);
    NdnEvent makeExpressInterestEvent(com.uofantarctica.jndn.proto.SyncStateProto.SyncState s, SyncDataHoarder
		    hoarder);
	Name getUniqueName(com.uofantarctica.jndn.proto.SyncStateProto.SyncState s);
	NdnTraffic makeInitPrefixTraffic(com.uofantarctica.jndn.proto.SyncStateProto.SyncState s, SyncDataHoarder
			hoarder);
}
