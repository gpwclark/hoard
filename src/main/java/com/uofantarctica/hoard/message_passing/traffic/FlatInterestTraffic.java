package com.uofantarctica.hoard.message_passing.traffic;

import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;
import com.uofantarctica.hoard.data_management.HoardServer;

public class FlatInterestTraffic implements NdnTraffic {
	private final Name prefix;
	private final Interest interest;
	private final Face face;
	private final long interestFilterId;
	private final InterestFilter filter;

	public FlatInterestTraffic(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
		this.prefix = prefix;
		this.interest = interest;
		this.face = face;
		this.interestFilterId = interestFilterId;
		this.filter = filter;
	}

	@Override
	public void process(HoardServer hoardServer) {
		hoardServer.processFlatInterest(prefix, interest, face, interestFilterId, filter);
	}

	@Override
	public String toString() {
		return "FlatInterestTraffic{" +
				"prefix=" + prefix.toUri() +
				", interest=" + interest.toUri() +
				", interestFilterId=" + interestFilterId +
				", filter=" + filter.getPrefix().toUri() +
				'}';
	}
}
