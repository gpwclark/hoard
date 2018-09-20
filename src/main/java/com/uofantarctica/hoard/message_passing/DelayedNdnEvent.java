package com.uofantarctica.hoard.message_passing;

import com.uofantarctica.hoard.message_passing.event.NdnEvent;
import com.uofantarctica.hoard.network_management.LocalFace;

public class DelayedNdnEvent extends NdnEvent {
	private final long delayMilliseconds;
	private final NdnEvent ndnEvent;

	public DelayedNdnEvent(long delayMilliseconds, NdnEvent ndnEvent) {
		this.delayMilliseconds = delayMilliseconds;
		this.ndnEvent = ndnEvent;
	}

	public long getDelay() {
		return delayMilliseconds;
	}

	@Override
	public void fire(LocalFace face) {
		ndnEvent.fire(face);
	}

	@Override
	public String getUniqueName() {
		return ndnEvent.getUniqueName();
	}
}
