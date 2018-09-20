package com.uofantarctica.hoard.network_management;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uofantarctica.dsync.DSync;
import com.uofantarctica.dsync.model.ReturnStrategy;
import com.uofantarctica.hoard.HoardThread;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.traffic.InitPrefixTraffic;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
import com.uofantarctica.hoard.protocols.HoardPrefixType;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.OnData;
import net.named_data.jndn.sync.ChronoSync2013;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Dequeue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;

public class NdnServer implements Runnable, HoardThread {
	private static final Logger log = LoggerFactory.getLogger(NdnServer.class);
	private static final String NDN_SERVER = "NDN_SERVER";

	private final LocalFace face;
	private final Dequeue<NdnEvent> deQNdnEvents;
	private final Enqueue<NdnTraffic> enQNdnTraffic;
	private final FederationInfo federationInfo;
	private DSync dsync;

	public static class FederationInfo {
		public final String theDataPrefix;
		public final String theBroadcastPrefix;
		public final String chatRoom;
		public final String screenName;

		public FederationInfo(String theDataPrefix, String theBroadcastPrefix, String chatRoom, String screenName) {
			this.theDataPrefix = theDataPrefix;
			this.theBroadcastPrefix = theBroadcastPrefix;
			this.chatRoom = chatRoom;
			this.screenName = screenName;
		}
	}

	public NdnServer(LocalFace face,
									 Dequeue<NdnEvent> deQNdnEvents,
									 Enqueue<NdnTraffic> enQNdnTraffic,
									 FederationInfo federationInfo) {
		this.face = face;
		this.deQNdnEvents = deQNdnEvents;
		this.enQNdnTraffic = enQNdnTraffic;
		this.federationInfo = federationInfo;
	}

	@Override
	public void run() {
		initFederation();
	    while (true) {
			try {
				face.processEvents();
				checkForNewNdnEvents();
			}
			catch(Exception e) {
	            log.error("Error in ndn server loop.", e);
			}
		}
	}

	private void initFederation() {
		this.dsync = new DSync(
			new OnData() {
				@Override
				public void onData(Interest interest, Data data) {
					try {
						HoardPrefixType.PrefixType prefixType = HoardPrefixType.PrefixType.parseFrom(data.getContent().getImmutableArray());
						enQNdnTraffic.enQ(new InitPrefixTraffic(prefixType));
					} catch (InvalidProtocolBufferException e) {
						log.error("failed to decode hoardServer prefix type: {}", data.getName().toUri());
					}
				}
			},
			new ChronoSync2013.OnInitialized() {
				@Override
				public void onInitialized() {
					log.debug("HoardServer prefix discovery dsync route initialized.");
				}
			},
			federationInfo.theDataPrefix,
			federationInfo.theBroadcastPrefix,
			System.currentTimeMillis(),
			face.getFace(),
			face.getKeyChain(),
			federationInfo.chatRoom,
			federationInfo.screenName,
			ReturnStrategy.EXACT);

		FederationProtocol federationProtocol = new DSyncFederation(dsync);
		face.setFederationProtocol(federationProtocol);
	}

	private void checkForNewNdnEvents() {
		NdnEvent newNdnEvent = deQNdnEvents.deQ();
		while (newNdnEvent != null) {
			newNdnEvent.fire(face);
			log.debug("Processing NdnEvents: {} ", newNdnEvent);
			newNdnEvent = deQNdnEvents.deQ();
		}
	}

	@Override
	public String getName() {
		return NDN_SERVER;
	}
}
