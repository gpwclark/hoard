package com.uofantarctica.hoard.message_passing;

public interface Queue<T> {
    T deQ();
    void enQ(T data);
}
