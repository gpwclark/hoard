package com.uofantarctica.hoard.message_passing.event;

import net.named_data.jndn.Data;
import com.uofantarctica.hoard.network_management.LocalFace;

import java.util.Objects;

public class PutData extends NdnEvent {
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

	public String getUniqueName() {
		return PutData.class.getSimpleName() + data.getName();
	}
}
