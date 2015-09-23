/*******************************************************************************
 * Copyright (C) PlaceIQ, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by placeiq 2015
 *******************************************************************************/
package org.swarm.commons;

import java.util.function.Supplier;

/**
 * Language level utils.
 */
public final class LangUtils {

    private LangUtils() {
        // Nothing
    }

    /**
     * Lazy value.
     */
    public static <T> ISupplier<T> lazy(Supplier<T> supplier) {
        return new ISupplier<T>() {
            T value;
            @Override public T get() {
                if (value == null) {
                    value = supplier.get();
                }
                return value;
            }
        };
    }

    /**
     * Strict value.
     */
    public static <T> ISupplier<T> strict(Supplier<T> supplier) {
        return new ISupplier<T>() {
            final T value = supplier.get();
            @Override public T get() {
                return value;
            }
        };
    }
}
