package com.uofantarctica.hoard.network_management;
import com.uofantarctica.hoard.HoardThread;
import com.uofantarctica.hoard.message_passing.Enqueue;
import com.uofantarctica.hoard.message_passing.traffic.NdnTraffic;
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

	public NdnServer(LocalFace face,
									 Dequeue<NdnEvent> deQNdnEvents, Enqueue<NdnTraffic> enQNdnTraffic) {
		this.face = face;
		this.deQNdnEvents = deQNdnEvents;
		this.enQNdnTraffic = enQNdnTraffic;
	}

	@Override
	public void run() {
	    while (true) {
			try {
				startDsync();
				face.processEvents();
				checkForNewNdnEvents();
			}
			catch(Exception e) {
	            log.error("Error in ndn server loop.", e);
			}
		}
	}

	private void startDsync() {
		face.startDsync(enQNdnTraffic);
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
