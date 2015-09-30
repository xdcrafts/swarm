package com.github.xdcrafts.swarm.commons;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Interface for function of two arguments.
 * @param <T>
 * @param <V>
 * @param <R>
 */
public interface IBiFunction<T, V, R> extends BiFunction<T, V , R> {

    /**
     * Partial application.
     */
    default Function<V, R> apply(T t) {
        return v -> IBiFunction.this.apply(t, v);
    }
}
