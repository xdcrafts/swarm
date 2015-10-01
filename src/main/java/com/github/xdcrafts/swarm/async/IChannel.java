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
     * @return boolean
     */
    boolean isClosed();

    /**
     * Async take from channel.
     * @return completable future with value
     */
    CompletableFuture<T> take();

    /**
     * Async put to channel.
     * @param value supplier of value of type I
     * @return completable future with optional, if optional is
     * empty than put is not done.
     */
    CompletableFuture<Optional<Supplier<T>>> put(Supplier<I> value);

    /**
     * Async put to channel.
     * @param future puts asynchronous value to channel
     * @return completable future with optional, if optional is
     * empty than put is not done.
     */
    default CompletableFuture<Optional<Supplier<T>>> put(CompletableFuture<I> future) {
        return future.thenCompose(value -> put(() -> value));
    }
}
