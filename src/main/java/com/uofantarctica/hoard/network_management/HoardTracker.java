package com.uofantarctica.hoard.network_management;

import com.uofantarctica.hoard.data_management.DataHoarder;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.NetworkNack;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnNetworkNack;
import net.named_data.jndn.OnTimeout;

public class HoardTracker implements OnData, OnTimeout, OnNetworkNack {
	private final LocalFace face;
	private final DataHoarder hoarder;
	public HoardTracker(LocalFace face, Interest interest, DataHoarder hoarder) {
		this.face = face;
		this.hoarder = hoarder;
		this.face.trackOutboundInterest(interest, hoarder);
	}

	@Override
	public void onData(Interest interest, Data data) {
		this.face.markInterestInbound(interest, hoarder);
		hoarder.onData(interest, data);
	}

	@Override
	public void onNetworkNack(Interest interest, NetworkNack networkNack) {
		this.face.markInterestInbound(interest, hoarder);
		hoarder.onNetworkNack(interest, networkNack);
	}

	@Override
	public void onTimeout(Interest interest) {
		this.face.markInterestInbound(interest, hoarder);
		hoarder.onTimeout(interest);
	}
}
