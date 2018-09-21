package com.uofantarctica.hoard;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import com.uofantarctica.hoard.protocols.HoardPrefixType;
import com.uofantarctica.jndn.sync_test_framework.ChatSimulation;
import com.uofantarctica.jndn.sync_test_framework.ChatSimulationBuilder;
import com.uofantarctica.jndn.sync_test_framework.UserChatSummary;
import com.uofantarctica.jndn.helpers.DockerTcpTransportFactory;
import com.uofantarctica.jndn.helpers.TransportConfiguration;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.encoding.WireFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class HoardTest {
	private static final Logger log = LoggerFactory.getLogger(HoardTest.class);

	final static String NFD_SERVICE = "nfd_service";
	final int INTERNAL_PORT = 6363;
	String host;
	int port;
	int numMessages = 10;
	int numParticipants = 2;
	HoardSimulation simulation;

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
		simulation
			= new HoardSimulation(host, port, numMessages, numParticipants);
		simulation.simulate();
	}


	@After
	public void tearDown() throws Exception {
		docker.containers().container(NFD_SERVICE).stop();
		simulation.hoard.interrupt();
	}

	@Test
	public void testCache() {
		log.debug("Running testcache");
		Set<Interest> uniqueInterests = new HashSet<>();
		for (Interest interest : simulation.chatSimulation.getAllInterests()) {
			Optional<Data> data = simulation.hoard.testCache(interest);
			assertTrue("Looking for interest: " + interest.toUri() + ", found no matching data.", data.isPresent());
			log.debug("found data for interest: {}", interest.toUri());
			uniqueInterests.add(interest);
			log.debug("Interest: {}.", interest.getName().toUri());
		}

		assertEquals("Interest must be equal to expected number of maessages + 1 extra because dsync oversubscribes.",
				UserChatSummary.getExpectedTotalCount(numParticipants, numMessages) + numParticipants,
				uniqueInterests.size());
	}
}