package com.uofantarctica.hoard.data_management;

import com.google.protobuf.InvalidProtocolBufferException;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import com.uofantarctica.hoard.network_management.ExponentialInterestBackoff;
import com.uofantarctica.hoard.protocols.ProcessSyncStates;
import com.uofantarctica.hoard.protocols.SyncPacket;
import com.uofantarctica.hoard.protocols.SyncStateProto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SyncDataHoarder extends DataHoarder {
	private static final Logger log = LoggerFactory.getLogger(SyncDataHoarder.class);

	public SyncDataHoarder(Enqueue<NdnEvent> ndnEvents,
						   Enqueue<NdnTraffic> ndnTraffic,
						   ExponentialInterestBackoff retryPolicy) {
		super(ndnEvents, ndnTraffic, retryPolicy);
	}

	@Override
	protected void processData(Interest interest, Data data) {
		log.debug("Got syncDataPacket and now processing: {} ", data.getName().toUri());
		expressInterestsIfDataFromKnownSyncProtocol(data);
	}


	private void expressInterestsIfDataFromKnownSyncProtocol(Data data) {
		List<NdnEvent> events = getInterestsIfDataFromKnownSyncProtocol(data);
		for (NdnEvent event : events) {
			ndnEvents.enQ(event);
		}
	}

	private List<NdnEvent> getInterestsIfDataFromKnownSyncProtocol(Data data) {
		List<NdnEvent> potentialEvents = new ArrayList<>();
		try {
			SyncStateProto.SyncStateMsg states = SyncStateProto.SyncStateMsg.parseFrom(data.getContent().getImmutableArray());
			SyncPacket packet = ProcessSyncStates.build(states);
			potentialEvents = getContents(packet);
		} catch (InvalidProtocolBufferException e) {
			log.error("Failed to get sync interests from: {}, unknown protocol?", data.getName(), e);
		    return potentialEvents;
		}

		return potentialEvents;
	}

	public List<NdnEvent> getContents(SyncPacket packet) {
		List<NdnEvent> events = new ArrayList<>();
		for (int i = 0; i < packet.getCount() ; i++) {
			SyncStateProto.SyncState s = packet.get(i);
			Name uniqueName = packet.getUniqueName(s);
			if (!isKnownSyncName(uniqueName)) {
				Name n = packet.makeName(s);
				events.add(packet.makeEvent(n, this));
				addSyncName(uniqueName);
			}
		}
		return events;
	}

	//TODO this needs to be way more intelligent.
	private final Set<String> knownSyncNames = new HashSet<>();
	public boolean isKnownSyncName(Name n) {
	    return knownSyncNames.contains(n.toUri());
	}

	public void addSyncName(Name n) {
	    knownSyncNames.add(n.toUri());
	}

	public void removeSyncName(Name n) {
		knownSyncNames.remove(n.toUri());
	}

	public Enqueue<NdnEvent> getNdnEvents() {
		return ndnEvents;
	}

	public Enqueue<NdnTraffic> getNdnTraffic() {
		return ndnTraffic;
	}
}
