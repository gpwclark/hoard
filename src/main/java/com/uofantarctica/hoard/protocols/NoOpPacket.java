package com.uofantarctica.hoard.protocols;

import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import net.named_data.jndn.Name;
import com.uofantarctica.hoard.data_management.SyncDataHoarder;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.jndn.proto.SyncStateProto;

public class NoOpPacket implements SyncPacket {
	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public SyncStateProto.SyncState get(int i) {
		return null;
	}

	@Override
	public NdnEvent makeExpressInterestEvent(SyncStateProto.SyncState s, SyncDataHoarder hoarder) {
		return null;
	}

	@Override
	public Name getUniqueName(SyncStateProto.SyncState s) {
		return null;
	}

	@Override
	public NdnTraffic makeInitPrefixTraffic(SyncStateProto.SyncState s, SyncDataHoarder hoarder) {
		return null;
	}
}
