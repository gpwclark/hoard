package com.uofantarctica.hoard.network_management;

import net.named_data.jndn.Data;

public interface FederationProtocol {
	void publishNextMessage(Data content);
}
