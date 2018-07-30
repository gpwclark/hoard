package com.uofantarctica.hoard.network_management;

import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.ForwardingFlags;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnInterestCallback;
import net.named_data.jndn.OnRegisterFailed;
import net.named_data.jndn.OnRegisterSuccess;
import net.named_data.jndn.encoding.WireFormat;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.util.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uofantarctica.hoard.data_management.DataHoarder;

import java.io.IOException;


public class LocalFace {
	private static final Logger log = LoggerFactory.getLogger(LocalFace.class);
	private final Face face;
	public LocalFace(Face face) {
		this.face = face;
	}

	public void expressInterest(Interest interest, DataHoarder hoarder) {
		try {
			face.expressInterest(interest, hoarder, hoarder, hoarder);
		} catch (IOException e) {
			log.error("Error expressInterest: {}", interest.getName().toUri(), e);
		}
	}

	public void putData(Data data) {
		try {
			face.putData(data);
		} catch (IOException e) {
			log.error("Error putData: {}", data.getName().toUri(), e);
		}
	}

	public void processEvents() {
		try {
			face.processEvents();
			Thread.sleep(10);
		} catch (Exception e) {
			log.error("Failed to processEvents",e);
		}
	}

	public void registerPrefix(Name prefix, OnInterestCallback onInterestCallback, OnRegisterFailed onRegisterFailed, OnRegisterSuccess onRegisterSuccess, ForwardingFlags flags, WireFormat wireFormat) {
		try {
			face.registerPrefix(prefix, onInterestCallback, onRegisterFailed, onRegisterSuccess, flags, wireFormat);
			log.debug("registerFlatDataPrefix: {}", prefix.toUri());
		} catch (IOException e) {
			log.error("Error registerFlatDataPrefix: {}", prefix.toUri(), e);
		} catch (SecurityException e) {
			log.error("Error registerFlatDataPrefix: {}", prefix.toUri(), e);
		}
	}

	public void send(Blob dataEncoding) {
		try {
			face.send(dataEncoding);
		} catch (IOException e) {
			log.error("Error send", e);
		}
	}

	public void callLater(double delay, Runnable action) {
		face.callLater(delay, action);
	}
}
