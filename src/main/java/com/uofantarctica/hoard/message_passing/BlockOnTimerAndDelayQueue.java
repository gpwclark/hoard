package com.uofantarctica.hoard.message_passing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

public class BlockOnTimerAndDelayQueue<T> implements Queue<T> {
	private static final Logger log = LoggerFactory.getLogger(BlockOnTimerAndDelayQueue.class);
	private final BlockingQueue<DelayObject> queue;
	private final long timeoutMs;
	private final TimeUnit timeUnit;

	public BlockOnTimerAndDelayQueue(long timeoutMs, TimeUnit timeUnit) {
		this.queue = new DelayQueue<>();
		this.timeoutMs = timeoutMs;
		this.timeUnit = timeUnit;
	}

	@Override
	public T deQ() {
		DelayObject element;
		try {
			element = queue.poll(timeoutMs, timeUnit);
			if (element != null) {
				return (T)element.getData();
			}
		} catch (InterruptedException e) {
			log.error("Error taking and returning data from queue.", e);
		}
		return null;
	}

	@Override
	public void enQ(T data) {
		long delayMilliseconds = getDelayMilliseconds(data);
		DelayObject<T> delayObject = new DelayObject<>(data, delayMilliseconds);
		try {
			queue.put(delayObject);
		} catch (InterruptedException e) {
			log.error("Interrupted while taking from queue.", e);
		}
	}

	private long getDelayMilliseconds(T data) {
		if (data instanceof DelayedNdnEvent) {
			return ((DelayedNdnEvent)data).getDelay();
		}
		else {
			return 0L;
		}
	}
}
