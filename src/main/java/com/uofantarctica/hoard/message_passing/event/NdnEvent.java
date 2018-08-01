package com.uofantarctica.hoard.message_passing.event;

import com.uofantarctica.hoard.network_management.LocalFace;

import java.util.Objects;

public abstract class NdnEvent {
	public abstract void fire(LocalFace face);
	abstract String getUniqueName();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NdnEvent ndnEvent = (NdnEvent) o;
		return Objects.equals(getUniqueName(), ndnEvent.getUniqueName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUniqueName());
	}
}
