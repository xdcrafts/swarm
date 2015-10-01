package com.github.xdcrafts.swarm.javaz.common.monoid;

/**
 * Monoid operations that can be applied for monoids of type M.
 * @param <T> value type
 * @param <M> monoid type
 */
public interface MonoidOps<T, M extends Monoid<?, M>> {
    /**
     * Zero value of this monoid.
     * @return monoid
     */
    M mempty();
    /**
     * Wraps pain value with monoid
     * @param t plain value
     * @return monoid
     */
    Monoid<T, M> wrap(T t);
}
