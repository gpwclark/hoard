package com.uofantarctica.hoard.message_passing.event;

import com.uofantarctica.hoard.network_management.LocalFace;
import com.uofantarctica.hoard.protocols.HoardPrefixType;

public class FederatedEvent extends NdnEvent {
	private final HoardPrefixType.PrefixType prefixType;

	public FederatedEvent(HoardPrefixType.PrefixType prefixType) {
		this.prefixType = prefixType;
	}

	@Override
	public void fire(LocalFace face) {
		face.publishFederatedEvent(prefixType);
	}

	@Override
	public String getUniqueName() {
		return "::name::" + prefixType.getName() + "::type::" + prefixType.getType();
	}
}
