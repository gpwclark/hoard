package com.uofantarctica.hoard.network_management;

import com.uofantarctica.dsync.DSync;
import net.named_data.jndn.Data;

public class DSyncFederation implements FederationProtocol {
	private final DSync dSync;
	public DSyncFederation(DSync dsync) {
		this.dSync = dsync;
	}

	@Override
	public void publishNextMessage(Data content) {
		dSync.publishNextMessage(content);
	}
}
