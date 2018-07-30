package com.uofantarctica.hoard.message_passing.event;

import com.uofantarctica.hoard.network_management.LocalFace;

public interface NdnEvent {
	void fire(LocalFace face);
}
