package com.github.xdcrafts.swarm.util.function;

import java.util.function.Consumer;

/**
 * Consumer that may throw exceptions.
 * @param <T> consumed value type.
 */
public interface IThrowingConsumer<T> {
    /**
     * Consumes value of type T.
     * @param t value
     * @throws Throwable possible exceptions
     */
    void consume(T t) throws Throwable;
    /**
     * Omits exceptions.
     * @return consumer
     */
    default Consumer<T> omit() {
        return t -> {
            try {
                consume(t);
            } catch (Throwable error) {
                throw new RuntimeException(error);
            }
        };
    }
}
