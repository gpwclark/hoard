package com.uofantarctica.hoard.message_passing.event;

import net.named_data.jndn.Name;
import net.named_data.jndn.util.Blob;
import com.uofantarctica.hoard.network_management.LocalFace;

public class SendEncoding extends NdnEvent {
	private final Blob dataEncoding;
	private final Name name;

	public SendEncoding(Blob dataEncoding, Name name) {
		this.dataEncoding = dataEncoding;
		this.name = name;
	}

	public void fire(LocalFace face) {
		face.send(dataEncoding, name);
	}


	@Override
	public String getUniqueName() {
		return SendEncoding.class.getSimpleName() + name.toUri();
	}

	@Override
	public String toString() {
		return "SendEncoding{" +
				"dataEncoding=" + dataEncoding.size() +"b" +
				'}';
	}

	public Blob getDataEncoding() {
		return dataEncoding;
	}

	public Name getName() {
		return name;
	}
}
