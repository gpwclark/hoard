package com.uofantarctica.hoard;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import com.uofantarctica.hoard.protocols.HoardPrefixType;
import com.uofantarctica.jndn.tests.chat.ChatSimulation;
import com.uofantarctica.jndn.tests.chat.ChatSimulationBuilder;
import com.uofantarctica.jndn.tests.chat.UserChatSummary;
import com.uofantarctica.jndn.tests.sync.DockerTcpTransportFactory;
import com.uofantarctica.jndn.tests.sync.TransportConfiguration;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.encoding.WireFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class HoardTest {

	final static String NFD_SERVICE = "nfd_service";
	final int INTERNAL_PORT = 6363;
	String host;
	int port;
	int numMessages = 10;
	int numParticipants = 2;
	UserChatSummary summary = null;
	ChatSimulation simulation;
	Hoard hoard;

	@ClassRule
	public static DockerComposeRule docker = DockerComposeRule.builder()
			.file("src/test/resources/docker-compose.yml")
			.waitingForService(NFD_SERVICE, HealthChecks.toHaveAllPortsOpen())
			.build();

	@Before
	public void setUp() throws Exception {
		WireFormat.getDefaultWireFormat();
		DockerPort nfd = docker.containers()
				.container(NFD_SERVICE)
				.port(INTERNAL_PORT);
		host = nfd.getIp();
		port = nfd.getExternalPort();
		TransportConfiguration.setTransportFactory(new DockerTcpTransportFactory(host, port));
	}


	@After
	public void tearDown() throws Exception {
		docker.containers().container(NFD_SERVICE).stop();
	}

	@Test
	public void testCache() {
		String screenName = "scratchy";
		String hubPrefix = "ndn/broadcast/hoard-chat";
		String defaultChatRoom = "ndnchat";
		String chatRoom = defaultChatRoom;
		String broadcastBaseName = "/ndn/broadcast/sync-simulation-test";

		hoard = Main.startDefaultHoard();
		HoardPrefixType.PrefixType.Builder prefixBuilder = HoardPrefixType.PrefixType.newBuilder();
		String routeName = hubPrefix + "/" + defaultChatRoom;

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
		simulation = builder.build();
		summary = simulation.simulate();
		for (Interest interest : simulation.getAllInterests()) {
			Optional<Data> data = hoard.testCache(interest);
			assertTrue("Every interest expressed should be in the hoard data cache", data.isPresent());
		}

		assertEquals("Interest must be equal to exected number of maessages",
				UserChatSummary.getExpectedTotalCount(numParticipants, numMessages),
				simulation.getAllInterests().size());
	}
}