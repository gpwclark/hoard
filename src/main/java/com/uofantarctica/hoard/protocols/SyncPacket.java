package com.uofantarctica.hoard.protocols;

import net.named_data.jndn.Name;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.data_management.SyncDataHoarder;

public interface SyncPacket {
    int getCount();
    com.uofantarctica.hoard.protocols.SyncStateProto.SyncState get(int i);
    NdnEvent makeEvent(Name n, SyncDataHoarder hoarder);
	Name makeName(com.uofantarctica.hoard.protocols.SyncStateProto.SyncState s);
	Name getUniqueName(com.uofantarctica.hoard.protocols.SyncStateProto.SyncState s);
}
