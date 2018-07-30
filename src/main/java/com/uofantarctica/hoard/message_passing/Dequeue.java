package com.uofantarctica.hoard.message_passing;

public class Dequeue<T> {
    private Queue<T> queue;

    public Dequeue(Queue<T> queue) {
        this.queue = queue;
    }

    public T deQ() {
        return queue.deQ();
    }
}
