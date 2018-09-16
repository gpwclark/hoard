package com.uofantarctica.hoard;

import com.uofantarctica.hoard.message_passing.traffic.InitPrefixTraffic;
import com.uofantarctica.hoard.protocols.HoardPrefixType;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	public static void main(String[] args) {
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.TRACE);
		log.debug("Starting.");

		Hoard hoard = startDefaultHoard();

		HoardPrefixType.PrefixType.Builder builder = HoardPrefixType.PrefixType.newBuilder();
		String routeName = "/ndn/broadcast/ChronoChat-0.3/ndnchat";
		builder.setName(routeName)
				.setType(HoardPrefixType.PrefixType.ActionType.DSYNC);
		HoardPrefixType.PrefixType prefixType = builder.build();
		hoard.addRoute(prefixType);
		/*
		routesToMonitor.add("/ndn/broadcast/ChronoChat-0.3");
		routesToMonitor.add("/ndn/broadcast/data");
		routesToMonitor.add("/ndn/broadcast/keys");
		routesToMonitor.add("/ndn/broadcast/atak");
		*/
		//routesToMonitor.add("/ndn/broadcast/");
		//routesToMonitor.add("/ndn/broadcast/");
		//routesToMonitor.add("/ndn/broadcast/edu/ucla/remap/ndnchat/");
	}

	public static Hoard startDefaultHoard() {
		HoardBuilder hoardBuilder = HoardBuilder.aHoard();
		return hoardBuilder.withChatRoom("/ndn/broadcast/data/hoardServer/")
				.withTheBroadcastPrefix("/ndn/broadcast/hoardServer/prefix_types")
				.withChatRoom("hoardServer-prefix-disocvery")
				.withScreenName("hoardServer-" + java.util.UUID.randomUUID().toString())
				.build();
	}
}
