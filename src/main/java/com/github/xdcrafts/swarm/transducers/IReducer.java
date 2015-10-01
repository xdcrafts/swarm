package com.github.xdcrafts.swarm.transducers;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Reducer is combination of 3 functions. Optional supplier of initial value.
 * Complete function that produces final result. Reducer function that gets
 * current result and next input and produces new result.
 * @param <T> input values type
 * @param <R> result type
 */
public interface IReducer<R, T> extends BiFunction<R, T, Reduction<R>> {

    /**
     * Supplier of initial value.
     * @return optional value of type R
     */
    default Optional<R> init() {
        return Optional.empty();
    }

    /**
     * Complete function.
     * @param value last reduction value
     * @return completed reduction
     */
    default Reduction<R> complete(Reduction<R> value) {
        return value;
    }

    /**
     * Reducer function.
     */
    @Override
    Reduction<R> apply(R result, T input);
}
