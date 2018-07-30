package com.uofantarctica.hoard.message_passing.event;

import net.named_data.jndn.Data;
import com.uofantarctica.hoard.network_management.LocalFace;

public class PutData implements NdnEvent {
	private final Data data;

	public PutData(Data data) {
		this.data = data;
	}

	@Override
	public void fire(LocalFace face) {
		face.putData(data);
	}

	@Override
	public String toString() {
		return "PutData{" +
				"data=" + data.getName() +
				'}';
	}
}
