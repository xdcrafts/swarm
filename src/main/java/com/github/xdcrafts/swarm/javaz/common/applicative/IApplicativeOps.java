package com.github.xdcrafts.swarm.javaz.common.applicative;

/**
 * Set of operations that available for Applicatives.
 * @param <M> applicative type
 */
public interface IApplicativeOps<M extends IApplicative<?, M>> {

    /**
     * Wraps value with applicative of type M.
     * @param value just plain value
     * @param <U> type of value
     * @return applicative
     */
    <U> M pure(U value);
}
