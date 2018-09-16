package com.uofantarctica.hoard.message_passing.traffic;

import com.uofantarctica.hoard.data_management.HoardServer;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;

public class FlatDataTraffic implements com.uofantarctica.hoard.message_passing.traffic.NdnTraffic {
	private final Interest interest;
	private final Data data;
	public FlatDataTraffic(Interest interest, Data data) {
		this.interest = interest;
		this.data = data;
	}

	@Override
	public void process(HoardServer hoardServer) {
		hoardServer.processFlatData(interest, data);
	}

	@Override
	public String toString() {
		return "FlatDataTraffic{" +
				"interest=" + interest.toUri() +
				", data=" + data.getName().toUri() +
				'}';
	}
}
