package org.swarm.transducers;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Interface for wrapped value that can be already reduced.
 * @param <T> value type
 */
public interface IReduction<T> extends Supplier<T> {

    /**
     * Is this value already reduced.
     */
    boolean isReduced();

    /**
     * Is this reduction failed with exception.
     */
    boolean isFailed();

    /**
     * Returns exception if occurred.
     */
    Optional<ReductionException> getError();
}
