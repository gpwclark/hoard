package com.uofantarctica.hoard.message_passing.traffic;

import com.uofantarctica.hoard.data_management.HoardServer;

public interface NdnTraffic {
	void process(HoardServer hoardServer);
}
