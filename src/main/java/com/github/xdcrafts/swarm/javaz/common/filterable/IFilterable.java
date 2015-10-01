package com.github.xdcrafts.swarm.javaz.common.filterable;

import java.util.function.Predicate;

/**
 * Interface for containers to which filtering can be applied.
 * @param <T> value type
 * @param <M> filterable type
 */
public interface IFilterable<T, M extends IFilterable<?, M>> {
    /**
     * Applies filter function to this filterable.
     * @param function predicate function
     * @return filtered filterable
     */
    M filter(Predicate<T> function);
}
