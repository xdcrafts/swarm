package org.swarm.commons;

import java.util.Optional;

/**
 * Utility functions for Optional.
 */
public final class OptionalUtils {

    private OptionalUtils() {
        // Nothing
    }

    /**
     * Flattens nested optionals.
     */
    public static <T> Optional<T> flatten(Optional<Optional<T>> optional) {
        return optional.orElse(Optional.<T>empty());
    }
}
