package com.uofantarctica.hoard.message_passing.event;

import net.named_data.jndn.Interest;
import com.uofantarctica.hoard.data_management.DataHoarder;
import com.uofantarctica.hoard.network_management.LocalFace;

public class ExpressInterest implements NdnEvent {
	private final Interest interest;
	private final DataHoarder dataHoarder;

	public ExpressInterest(Interest interest, DataHoarder hoarder) {
		this.interest = interest;
		this.dataHoarder = hoarder;
	}

	@Override
	public void fire(LocalFace face) {
		face.expressInterest(interest, dataHoarder);
	}

	@Override
	public String toString() {
		return "ExpressInterest{" +
				"interest=" + interest.toUri() +
				'}';
	}
}

