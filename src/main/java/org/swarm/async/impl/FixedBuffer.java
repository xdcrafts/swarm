package org.swarm.async.impl;

import org.swarm.async.IBuffer;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fixed buffer implementation.
 * @param <T> value type
 */
public class FixedBuffer<T> implements IBuffer<T> {

    private final ConcurrentLinkedQueue<T> values = new ConcurrentLinkedQueue<>();
    private final int capacity;

    private final AtomicInteger currentSize = new AtomicInteger();

    public FixedBuffer(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Buffer capacity must be greater then zero");
        }
        this.capacity = capacity;
    }

    @Override
    public synchronized boolean add(T value) {
        if (!isFull()) {
            this.values.offer(value);
            this.currentSize.incrementAndGet();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized Optional<T> remove() {
        this.currentSize.decrementAndGet();
        return Optional.ofNullable(this.values.poll());
    }

    @Override
    public boolean isFull() {
        return this.currentSize.get() >= this.capacity;
    }

    @Override
    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    @Override
    public int size() {
        return this.currentSize.get();
    }
}
