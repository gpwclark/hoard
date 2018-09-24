package com.uofantarctica.hoard.message_passing;

import com.google.common.primitives.Ints;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayObject<T> implements Delayed {
	private final T t;
	private final long minStartTime;

	public DelayObject(T t, long delayMilliseconds) {
		this.t = t;
		this.minStartTime = System.currentTimeMillis() + delayMilliseconds;
	}

	@Override
	public long getDelay(TimeUnit timeUnit) {
		long diff = minStartTime - System.currentTimeMillis();
		return timeUnit.convert(diff, TimeUnit.MILLISECONDS);
	}

	@Override
	public int compareTo(Delayed delayed) {
		return Ints.saturatedCast(
			this.minStartTime - ((DelayObject) delayed).minStartTime);
	}

	public T getData() {
		return t;
	}
}
