package com.uofantarctica.hoard.message_passing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NonBlockingQueue<T> implements Queue<T> {
	private static final Logger log = LoggerFactory.getLogger(NonBlockingQueue.class);
	ConcurrentLinkedQueue<T> queue;

	public NonBlockingQueue() {
		this.queue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public T deQ() {
		T data = null;
		try {
			data = (T)this.queue.poll();
		} catch (Exception e) {
			log.error("Failed to deQ", e);
		}
		return data;
	}

	@Override
	public void enQ(T data) {
		this.queue.add(data);
	}
}
