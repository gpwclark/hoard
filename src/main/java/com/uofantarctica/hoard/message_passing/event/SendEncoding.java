package com.uofantarctica.hoard.message_passing.event;

import net.named_data.jndn.util.Blob;
import com.uofantarctica.hoard.network_management.LocalFace;

public class SendEncoding implements NdnEvent {
	private final Blob dataEncoding;
	public SendEncoding(Blob dataEncoding) {
		this.dataEncoding = dataEncoding;
	}

	public void fire(LocalFace face) {
		face.send(dataEncoding);
	}

	@Override
	public String toString() {
		return "SendEncoding{" +
				"dataEncoding=" + dataEncoding.size() +"b" +
				'}';
	}
}
