package com.uofantarctica.hoard.message_passing.traffic;

import com.uofantarctica.hoard.data_management.Hoard;

public interface NdnTraffic {
	void process(Hoard hoard);
}
