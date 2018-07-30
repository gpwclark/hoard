package com.uofantarctica.hoard.network_management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.message_passing.Dequeue;
import com.uofantarctica.hoard.message_passing.event.NdnEvent;

public class NdnServer implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(NdnServer.class);

	private final LocalFace face;
	private final Dequeue<NdnEvent> ndnEvents;

	public NdnServer(LocalFace face,
					 Dequeue<NdnEvent> ndnEvents) {
		this.face = face;
		this.ndnEvents = ndnEvents;
	}

	@Override
	public void run() {
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

	private void checkForNewNdnEvents() {
		NdnEvent newNdnEvent = ndnEvents.deQ();
		while (newNdnEvent != null) {
			newNdnEvent.fire(face);
			log.debug("Processing NdnEvents: {} ", newNdnEvent);
			newNdnEvent = ndnEvents.deQ();
		}
	}
}
