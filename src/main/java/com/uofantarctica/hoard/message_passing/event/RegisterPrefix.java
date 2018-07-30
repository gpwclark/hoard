package com.uofantarctica.hoard.message_passing.event;

import net.named_data.jndn.ForwardingFlags;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnInterestCallback;
import net.named_data.jndn.OnRegisterFailed;
import net.named_data.jndn.OnRegisterSuccess;
import net.named_data.jndn.encoding.WireFormat;
import com.uofantarctica.hoard.data_management.SyncInterestListener;
import com.uofantarctica.hoard.network_management.LocalFace;

public class RegisterPrefix implements NdnEvent {
	private final Name prefix;
	private final OnInterestCallback onInterestCallback;
	private final OnRegisterFailed onRegisterFailed;
	private final OnRegisterSuccess onRegisterSuccess;
	private final ForwardingFlags flags;
	private final WireFormat wireFormat;

	public RegisterPrefix(Name prefix, OnInterestCallback onInterestCallback, OnRegisterFailed onRegisterFailed, OnRegisterSuccess onRegisterSuccess) {
		this.prefix = prefix;
		this.onInterestCallback = onInterestCallback;
		this.onRegisterFailed = onRegisterFailed;
		this.onRegisterSuccess = onRegisterSuccess;
		this.flags = new ForwardingFlags();
		flags.setChildInherit(true);
		flags.setCapture(false);
		this.wireFormat = WireFormat.getDefaultWireFormat();
	}

    public RegisterPrefix(Name name, SyncInterestListener syncInterestListener) {
	    this(name, syncInterestListener, syncInterestListener, syncInterestListener);
    }

    @Override
	public void fire(LocalFace face) {
		face.registerPrefix(prefix, onInterestCallback, onRegisterFailed, onRegisterSuccess, flags, wireFormat);
	}

	@Override
	public String toString() {
		return "RegisterPrefix{" +
				"prefix=" + prefix.toUri() +
				'}';
	}
}
