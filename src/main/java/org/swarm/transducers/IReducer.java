package org.swarm.transducers;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Reducer is combination of 3 functions. Optional supplier of initial value.
 * Complete function that produces final result. Reducer function that gets
 * current result and next input and produces new result.
 * @param <T> input values type
 * @param <R> result type
 */
public interface IReducer<R, T> extends BiFunction<R, T, IReduction<R>> {

    /**
     * Supplier of initial value.
     */
    default Optional<Supplier<R>> init() {
        return Optional.empty();
    }

    /**
     * Complete function.
     */
    default R complete(R value) {
        return value;
    }

    /**
     * Reducer function.
     */
    @Override
    IReduction<R> apply(R result, T value);
}
