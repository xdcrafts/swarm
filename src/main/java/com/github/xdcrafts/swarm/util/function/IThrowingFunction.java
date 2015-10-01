package com.github.xdcrafts.swarm.util.function;

import com.github.xdcrafts.swarm.transducers.ReductionException;
import com.github.xdcrafts.swarm.util.function.IFunction;

/**
 * Function that may throw exceptions.
 * @param <A> argument type
 * @param <B> return type
 */
public interface IThrowingFunction<A, B> {
    /**
     * Applies function to argument a.
     * @param a argument
     * @return value of type B
     * @throws Throwable possible exceptions
     */
    B apply(A a) throws Throwable;
    /**
     * Omits exceptions.
     * @return function
     */
    default IFunction<A, B> omit() {
        return a -> {
            try {
                return apply(a);
            } catch (Throwable t) {
                throw new ReductionException(t);
            }
        };
    }
}
