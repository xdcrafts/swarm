package com.github.xdcrafts.swarm.commons;

/**
 * Utility functions for arrays.
 */
public final class ArrayUtils {

    private ArrayUtils() {
    }

    /**
     * Just wrapper for infinite argument semantics.
     */
    public static <T> T[] asArray(T... ts) {
        return ts;
    }
}
