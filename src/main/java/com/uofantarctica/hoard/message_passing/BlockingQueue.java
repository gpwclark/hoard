package com.uofantarctica.hoard.message_passing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueue<T> implements Queue<T> {
	private static final Logger log = LoggerFactory.getLogger(BlockingQueue.class);

	LinkedBlockingQueue<T> queue;
	private final long timeout;

	public BlockingQueue(long timeout) {
		this.queue = new LinkedBlockingQueue<>();
		this.timeout = timeout;
	}

	public BlockingQueue() {
		this.queue = new LinkedBlockingQueue<>();
		timeout = 5l;
	}

	@Override
	public T deQ() {
		T data = null;
		try {
			data = (T)this.queue.poll(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error("Failed to deQ", e);
		}
		return data;
	}

    @Override
	public void enQ(T data) {
		this.queue.add(data);
	}
}
