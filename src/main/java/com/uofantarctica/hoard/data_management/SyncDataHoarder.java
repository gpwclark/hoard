package com.uofantarctica.hoard.data_management;

import com.google.protobuf.InvalidProtocolBufferException;
import com.uofantarctica.hoard.network_management.ExponentialBackoff;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import com.uofantarctica.hoard.protocols.ProcessSyncStates;
import com.uofantarctica.hoard.protocols.SyncPacket;
import com.uofantarctica.jndn.proto.SyncStateProto;

import java.util.HashSet;
import java.util.Set;

public class SyncDataHoarder extends DataHoarder {
	private static final Logger log = LoggerFactory.getLogger(SyncDataHoarder.class);

	private final MemoryContentCache cache;
	public SyncDataHoarder(Enqueue<NdnEvent> ndnEvents,
						   Enqueue<NdnTraffic> ndnTraffic,
						   ExponentialBackoff retryPolicy,
	                       MemoryContentCache cache) {
		super(ndnEvents, ndnTraffic, retryPolicy);
		this.cache = cache;
	}

	public MemoryContentCache getCache() {
		return cache;
	}

	public Enqueue<NdnEvent> getEnQNdnEvent() {
		return enQNdnEvents;
	}

	public Enqueue<NdnTraffic> getEnQNdnTraffic() {
		return ndnTraffic;
	}

	public ExponentialBackoff getRetryPolicy() {
		return retryPolicy;
	}

	@Override
	protected void processData(Interest interest, Data data) {
		log.debug("Got syncDataPacket and now processing: {} ", data.getName().toUri());
		expressInterestsIfDataFromKnownSyncProtocol(data);
	}


	private void expressInterestsIfDataFromKnownSyncProtocol(Data data) {
		try {
			SyncStateProto.SyncStateMsg states = SyncStateProto.SyncStateMsg.parseFrom(data.getContent().getImmutableArray());
			SyncPacket packet = ProcessSyncStates.build(states);
			getContents(packet);
		} catch (InvalidProtocolBufferException e) {
			log.error("Failed to get sync interests from: {}, unknown protocol?", data.getName(), e);
		}
	}

	public void getContents(SyncPacket packet) {
		for (int i = 0; i < packet.getCount() ; i++) {
			SyncStateProto.SyncState s = packet.get(i);
			Name uniqueName = packet.getUniqueName(s);
			if (!isKnownSyncName(uniqueName)) {
				//Name n = packet.makeExpressInterestName(s);
				enQNdnEvents.enQ(packet.makeExpressInterestEvent(s, this));
				ndnTraffic.enQ(packet.makeInitPrefixTraffic(s, this));
				addSyncName(uniqueName);
			}
		}
	}

	//TODO this needs to be way more intelligent.
	private final Set<String> knownSyncNames = new HashSet<>();
	public boolean isKnownSyncName(Name n) {
	    return knownSyncNames.contains(n.toUri());
	}

	public void addSyncName(Name n) {
		log.debug("===================================");
		log.debug("New sync name added: {}", n.toUri());
		log.debug("===================================");
	    knownSyncNames.add(n.toUri());
	}

	public void removeSyncName(Name n) {
		knownSyncNames.remove(n.toUri());
	}

	public Enqueue<NdnEvent> getNdnEvents() {
		return enQNdnEvents;
	}

	public Enqueue<NdnTraffic> getNdnTraffic() {
		return ndnTraffic;
	}
}
