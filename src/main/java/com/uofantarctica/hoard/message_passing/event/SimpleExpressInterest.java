package com.uofantarctica.hoard.message_passing.event;

import net.named_data.jndn.Interest;
import com.uofantarctica.hoard.data_management.DataHoarder;
import com.uofantarctica.hoard.network_management.LocalFace;

public class SimpleExpressInterest extends ExpressInterest {

	public SimpleExpressInterest(Interest interest, DataHoarder hoarder) {
		super (interest, hoarder);
	}

	@Override
	public void fire(LocalFace face) {
		face.expressInterest(this);
	}

	@Override
	public String toString() {
		return "SimpleExpressInterest{" +
				"interest=" + interest.toUri() +
				'}';
	}

	@Override
	public String getUniqueName() {
		return SimpleExpressInterest.class.getSimpleName() + interest.getName().toUri();
	}
}

