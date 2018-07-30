package com.uofantarctica.hoard.protocols;

import net.named_data.jndn.Name;
import com.uofantarctica.hoard.data_management.SyncDataHoarder;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;

public class NoOpPacket implements SyncPacket {
	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public com.uofantarctica.hoard.protocols.SyncStateProto.SyncState get(int i) {
		return null;
	}

	@Override
	public NdnEvent makeEvent(Name n, SyncDataHoarder hoarder) {
		return null;
	}

	@Override
	public Name makeName(com.uofantarctica.hoard.protocols.SyncStateProto.SyncState s) {
		return null;
	}

	@Override
	public Name getUniqueName(com.uofantarctica.hoard.protocols.SyncStateProto.SyncState s) {
		return null;
	}
}
