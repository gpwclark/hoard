package com.uofantarctica.hoard.message_passing.event;

import net.named_data.jndn.Interest;
import com.uofantarctica.hoard.network_management.LocalFace;

public class ExpressDelayedInterest implements NdnEvent {
	private final Runnable delayedNdnEvent;
	private final double delay;
	private final Interest interest;

	public ExpressDelayedInterest(Runnable delayedNdnEvent, double delay, Interest interest) {
		this.delayedNdnEvent = delayedNdnEvent;
		this.delay = delay;
		this.interest = interest;
	}

	@Override
	public void fire(LocalFace face) {
		face.callLater(delay, delayedNdnEvent);
	}

	@Override
	public String toString() {
		return "ExpressDelayedInterest{" +
				"delayedNdnEvent=" + delayedNdnEvent +
				", delay=" + delay +
				", interest=" + interest +
				'}';
	}
}
