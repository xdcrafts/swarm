package com.github.xdcrafts.swarm.util;

/**
 * Utility functions for arrays.
 */
public final class ArrayUtils {

    private ArrayUtils() {
    }

    /**
     * Just wrapper for infinite argument semantics.
     * @param <T> values type
     * @param ts variadic parameters
     * @return array of type T
     */
    public static <T> T[] asArray(T... ts) {
        return ts;
    }
}
