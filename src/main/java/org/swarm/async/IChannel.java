package org.swarm.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Async channel.
 * @param <T> value type
 */
public interface IChannel<T> {

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
     * Async put tu channel.
     */
    CompletableFuture<T> put(Supplier<T> value);
}
