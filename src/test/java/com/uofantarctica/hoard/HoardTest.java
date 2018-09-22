package com.uofantarctica.hoard;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.Container;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import com.uofantarctica.jndn.helpers.DockerTcpTransportFactory;
import com.uofantarctica.jndn.helpers.TransportConfiguration;
import com.uofantarctica.jndn.sync_test_framework.UserChatSummary;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
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
	HoardDataCollectionSimulation simulation;

	@ClassRule
	public static DockerComposeRule docker = DockerComposeRule.builder()
			.file("src/test/resources/docker-compose.yml")
			.waitingForService(NFD_SERVICE, HealthChecks.toHaveAllPortsOpen())
			.build();

	@Before
	public void setUp() throws Exception {
		WireFormat.getDefaultWireFormat();
		hookUpFaceToContainer();
		simulation
			= new HoardDataCollectionSimulation(numMessages, numParticipants);
		simulation.simulate();
	}

	public Container getNfdContainer() {
		return docker.containers().container(NFD_SERVICE);
	}

	private void hookUpFaceToContainer() {
		DockerPort nfd = getNfdContainer().port(INTERNAL_PORT);
		host = nfd.getIp();
		port = nfd.getExternalPort();
		TransportConfiguration.setTransportFactory(new DockerTcpTransportFactory(host, port));
	}


	@After
	public void tearDown() throws Exception {
		getNfdContainer().stop();
		//simulation.hoard.interrupt();
	}

	@Test
	public void testCache() {
		log.debug("Running testcache");
		List<Interest> interestsExpressed
			= simulation.chatSimulation.getAllInterests();

		testCacheHasInterests(interestsExpressed);
	}

	private void testCacheHasInterests(List<Interest> interestsExpressed) {
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

	@Test
	public void testHoardWhenNfdRestarts() {
		restart(30, TimeUnit.SECONDS);
		List<Interest> firstRoundInterestsExpressed
			= simulation.chatSimulation.getAllInterests();

		simulation.newDataCollectionPhase();
		testCacheHasInterests(firstRoundInterestsExpressed);
		List<Interest> secondRoundInterestsExpressed
			= simulation.chatSimulation.getAllInterests();
		testCacheHasInterests(secondRoundInterestsExpressed);
	}

	private void restart(int timeout, TimeUnit unit) {
		restartNfd(timeout, unit);
		hookUpFaceToContainer();
	}

	private void restartNfd(long timeout, TimeUnit unit) {
		try {
			docker.containers().container(NFD_SERVICE).kill();
			//TODO prefer stop to kill... something to do with network interfaces being taken down properly...
			//docker.containers().container(NFD_SERVICE).stop();
			getNfdContainer().start();

			nfdContainerHasRestarted();
			await().atMost(timeout, unit).until(() -> nfdContainerHasRestarted());

		} catch (Exception e) {
			log.error("failed to restart nfd", e);
		}
	}

	private boolean nfdContainerHasRestarted() {
		boolean isFinishedRestarting = getNfdContainer()
			.portMappedExternallyTo(port)
			.isListeningNow();
		return isFinishedRestarting;
	}
}