package com.uofantarctica.hoard.message_passing.event;

import com.uofantarctica.hoard.data_management.DataHoarder;
import net.named_data.jndn.Interest;

public abstract class ExpressInterest extends NdnEvent {
	protected final Interest interest;
	protected final DataHoarder hoarder;

	public ExpressInterest(Interest interest, DataHoarder hoarder) {
		this.interest = interest;
		this.hoarder = hoarder;
	}

	public DataHoarder getHoarder() {
		return hoarder;
	}

	public Interest getInterest() {
		return interest;
	}
}
