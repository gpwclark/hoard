package com.uofantarctica.hoard.message_passing;

public class Enqueue<T> {
    private Queue<T> queue;

    public Enqueue(Queue<T> queue) {
        this.queue = queue;
    }

    public void enQ(T data) {
        queue.enQ(data);
    }
}
