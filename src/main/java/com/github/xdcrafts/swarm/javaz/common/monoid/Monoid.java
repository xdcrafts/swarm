package com.github.xdcrafts.swarm.javaz.common.monoid;

/**
 * Monoid interface of something that can be composed and has zero value.
 * @param <T> monoid value type
 * @param <M> monoid type
 */
public interface Monoid<T, M extends Monoid<?, M>> {
    /**
     * Composes two monoids.
     * @param another another monoid
     * @return composite monoid
     */
    M mappend(Monoid<T, M> another);
    /**
     * Extracts plain value from monoid.
     * @return value of type T
     */
    T value();
    /**
     * Returns monoid operations associated with this type of monoid.
     * @return monoid operations
     */
    MonoidOps<T, M> ops();
}
