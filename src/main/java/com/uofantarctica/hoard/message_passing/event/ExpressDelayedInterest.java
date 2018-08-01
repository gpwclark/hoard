package com.uofantarctica.hoard.message_passing.event;

import com.uofantarctica.hoard.data_management.DataHoarder;
import net.named_data.jndn.Interest;
import com.uofantarctica.hoard.network_management.LocalFace;

public class ExpressDelayedInterest extends ExpressInterest {
	private final Runnable delayedNdnEvent;
	private final double delay;
	private final Interest interest;

	public ExpressDelayedInterest(Runnable delayedNdnEvent, double delay, Interest interest, DataHoarder hoarder) {
		super(interest, hoarder);
		this.delayedNdnEvent = delayedNdnEvent;
		this.delay = delay;
		this.interest = interest;
	}

	@Override
	public void fire(LocalFace face) {
		face.callLater(delay, delayedNdnEvent);
	}

	@Override
	public String getUniqueName() {
		return SimpleExpressInterest.class.getSimpleName() + interest.getName().toUri();
	}

	@Override
	public String toString() {
		return "ExpressDelayedInterest{" +
				", interest=" + interest.toUri() +
				", delay=" + delay +
				'}';
	}
}
