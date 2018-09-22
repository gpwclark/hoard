package com.uofantarctica.hoard;

import com.uofantarctica.hoard.protocols.HoardPrefixType;
import com.uofantarctica.jndn.sync_test_framework.ChatSimulation;
import com.uofantarctica.jndn.sync_test_framework.ChatSimulationBuilder;
import com.uofantarctica.jndn.sync_test_framework.UserChatSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class HoardDataCollectionSimulation {
	private static final Logger log = LoggerFactory.getLogger(HoardDataCollectionSimulation.class);

	private final int numMessages;
	private final int numParticipants;
	UserChatSummary summary;
	public Hoard hoard;
	public ChatSimulation chatSimulation;
	Thread hoardThread;
	String screenName = "scratchy";
	String hubPrefix = "ndn/broadcast/data-namespace";
	String chatRoomPrefix = "ndnchat";
	String broadcastBaseName = "/ndn/broadcast/sync-namespace";


	public HoardDataCollectionSimulation(int numMessages, int numParticipants) {
		this.numMessages = numMessages;
		this.numParticipants = numParticipants;
	}

	public void simulate() {
		startHoard();
		newDataCollectionPhase();
	}

	private void startHoard() {
		hoard = Main.startDefaultHoard();
		hoardThread = new Thread(hoard);
		hoardThread.start();
	}

	public void newDataCollectionPhase() {
		HoardPrefixType.PrefixType.Builder prefixBuilder = HoardPrefixType.PrefixType.newBuilder();

		String chatRoom = chatRoomPrefix + UUID.randomUUID().toString();
		String routeName = broadcastBaseName + "/" + chatRoom;

		prefixBuilder.setName(routeName)
			.setType(HoardPrefixType.PrefixType.ActionType.DSYNC);
		HoardPrefixType.PrefixType prefixType = prefixBuilder.build();
		hoard.addRoute(prefixType);

		ChatSimulationBuilder builder = ChatSimulationBuilder.aChatSimulation();
		builder.withScreenName(screenName)
			.withBroadcastBaseName(broadcastBaseName)
			.withHubPrefix(hubPrefix)
			.withChatRoom(chatRoom)
			.withNumMessages(numMessages)
			.withNumParticipants(numParticipants);
		chatSimulation = builder.build();
		summary = chatSimulation.simulate();
	}
}
