package com.uofantarctica.hoard;

import com.uofantarctica.hoard.protocols.HoardPrefixType;
import com.uofantarctica.jndn.helpers.DockerTcpTransportFactory;
import com.uofantarctica.jndn.helpers.TransportConfiguration;
import com.uofantarctica.jndn.sync_test_framework.ChatSimulation;
import com.uofantarctica.jndn.sync_test_framework.ChatSimulationBuilder;
import com.uofantarctica.jndn.sync_test_framework.UserChatSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HoardSimulation {
	private static final Logger log = LoggerFactory.getLogger(HoardSimulation.class);

	private final String host;
	private final int port;
	private final int numMessages;
	private final int numParticipants;
	UserChatSummary summary;
	public Hoard hoard;
	public ChatSimulation chatSimulation;
	Thread hoardThread;

	public HoardSimulation(String host, int port, int numMessages, int numParticipants) {
		this.host = host;
		this.port = port;
		this.numMessages = numMessages;
		this.numParticipants = numParticipants;
	}

	public void simulate() {
		TransportConfiguration.setTransportFactory(new DockerTcpTransportFactory(host, port));

		String screenName = "scratchy";
		String hubPrefix = "ndn/broadcast/data-namespace";
		String defaultChatRoom = "ndnchat";
		String chatRoom = defaultChatRoom;
		String broadcastBaseName = "/ndn/broadcast/sync-namespace";

		hoard = Main.startDefaultHoard();
		hoardThread = new Thread(hoard);
		hoardThread.start();
		HoardPrefixType.PrefixType.Builder prefixBuilder = HoardPrefixType.PrefixType.newBuilder();
		String routeName = broadcastBaseName + "/" + defaultChatRoom;

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
