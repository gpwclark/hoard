package com.uofantarctica.hoard.network_management;

import com.uofantarctica.hoard.data_management.DataHoarder;
import com.uofantarctica.hoard.message_passing.event.ExpressInterest;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.NetworkNack;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnNetworkNack;
import net.named_data.jndn.OnTimeout;

public class InterestTracker implements OnData, OnTimeout, OnNetworkNack {
	private final LocalFace face;
	private final ExpressInterest expressInterest;
	private final DataHoarder hoarder;

	public InterestTracker(LocalFace face, ExpressInterest expressInterest) {
		this.face = face;
		this.expressInterest = expressInterest;
		this.hoarder = expressInterest.getHoarder();
		this.face.trackOutboundInterest(expressInterest);
	}

	@Override
	public void onData(Interest interest, Data data) {
		hoarder.onData(interest, data);
		this.face.markInterestInbound(expressInterest);
	}

	@Override
	public void onNetworkNack(Interest interest, NetworkNack networkNack) {
		hoarder.onNetworkNack(interest, networkNack);
		this.face.markInterestInbound(expressInterest);
	}

	@Override
	public void onTimeout(Interest interest) {
		hoarder.onTimeout(interest);
		this.face.markInterestInbound(expressInterest);
	}
}
