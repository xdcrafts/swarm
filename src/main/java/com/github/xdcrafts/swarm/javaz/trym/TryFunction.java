package com.github.xdcrafts.swarm.javaz.trym;

import com.github.xdcrafts.swarm.util.function.IFunction;
import com.github.xdcrafts.swarm.util.function.IThrowingFunction;

/**
 * Function that is able to produce try monad in case of failure.
 * @param <A> input type
 * @param <B> output type
 */
public final class TryFunction<A, B> implements IFunction<A, ITryM<B>> {

    private final IThrowingFunction<A, B> function;

    /**
     * Converts throwing function to try function.
     * @param function function to convert
     * @param <A> input type
     * @param <B> return type
     * @return try function
     */
    public static <A, B> TryFunction<A, B> tryFunction(IThrowingFunction<A, B> function) {
        return new TryFunction<>(function);
    }

    public TryFunction(IThrowingFunction<A, B> function) {
        if (function == null) {
            throw new IllegalArgumentException("IFunction for TryFunction<A, B> can not be null!");
        }
        this.function = function;
    }

    @Override
    public ITryM<B> apply(A a) {
        return TryMOps.tryM(this.function, a);
    }
}
