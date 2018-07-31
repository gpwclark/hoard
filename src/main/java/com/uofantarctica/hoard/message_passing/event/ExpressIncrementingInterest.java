package com.uofantarctica.hoard.message_passing.event;

import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import com.uofantarctica.hoard.data_management.DataHoarder;
import com.uofantarctica.hoard.network_management.LocalFace;

public class ExpressIncrementingInterest implements NdnEvent {
	private final Interest interest;
	private final DataHoarder hoarder;

    public ExpressIncrementingInterest(Name n, DataHoarder hoarder) {
    	this.interest = new Interest(n);
    	this.hoarder = hoarder;
    }

	public ExpressIncrementingInterest(Interest i, DataHoarder hoarder) {
		this.interest = i;
		this.hoarder = hoarder;
	}

	@Override
    public void fire(LocalFace face) {
	    face.expressInterest(interest, hoarder);
    }

	@Override
	public String toString() {
		return "ExpressIncrementingInterest{" +
				"interest=" + interest.getName().toUri() +
				", hoarder=" + hoarder +
				'}';
	}
}
