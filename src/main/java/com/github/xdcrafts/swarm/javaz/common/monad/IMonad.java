package com.github.xdcrafts.swarm.javaz.common.monad;

import com.github.xdcrafts.swarm.javaz.common.applicative.IApplicative;

import java.util.function.Function;

/**
 * Monad interface that can apply to contained value function that returns another value wrapped with monad.
 * @param <T> value type
 * @param <M> monad type
 */
public interface IMonad<T, M extends IMonad<?, M>> extends IApplicative<T, M> {
    /**
     * Apply function to contained value and returns new monad instance with new value of type U.
     * @param function monadic function
     * @param <U> new value type
     * @param <MM> monad type
     * @return new value of type U wrapped with monad M
     */
    <U, MM extends IMonad<U, M>> M flatMap(Function<T, MM> function);
}
