package com.uofantarctica.hoard.message_passing.event;

import com.uofantarctica.hoard.data_management.DataHoarder;
import net.named_data.jndn.Interest;
import com.uofantarctica.hoard.network_management.LocalFace;

public class ExpressSingleInterest extends ExpressInterest {
	private final Interest interest;
	private final DataHoarder dataHoarder;

	public ExpressSingleInterest(Interest interest, DataHoarder hoarder) {
		super(interest, hoarder);
		this.interest = interest;
		this.dataHoarder = hoarder;
	}

	@Override
	public void fire(LocalFace face) {
		face.expressInterest(this);
	}

	@Override
	public String getUniqueName() {
		return ExpressSingleInterest.class.getSimpleName() + interest.getName().toUri();
	}

	@Override
	public String toString() {
		return "ExpressSingleInterest{" +
				", interest=" + interest.toUri() +
				'}';
	}
}
