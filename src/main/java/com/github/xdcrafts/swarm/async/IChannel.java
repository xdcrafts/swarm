package com.github.xdcrafts.swarm.async;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Async channel.
 * @param <T> value type
 * @param <I> input value type
 */
public interface IChannel<T, I> {

    /**
     * Close channel.
     */
    void close();

    /**
     * Is this channel already closed.
     */
    boolean isClosed();

    /**
     * Async take from channel.
     */
    CompletableFuture<T> take();

    /**
     * Async put to channel.
     */
    CompletableFuture<Optional<Supplier<T>>> put(Supplier<I> value);

    /**
     * Async put to channel.
     */
    default CompletableFuture<Optional<Supplier<T>>> put(CompletableFuture<I> future) {
        return future.thenCompose(value -> put(() -> value));
    }
}
