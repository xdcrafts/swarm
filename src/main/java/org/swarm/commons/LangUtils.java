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
public class LangUtils {

    /**
     * Lazy value.
     */
    public static <T> Supplier<T> lazy(Supplier<T> supplier) {
        return new Supplier<T>() {
            T value;
            @Override public T get() {
                if (value == null) {
                    value = supplier.get();
                }
                return value;
            }
        };
    }
}
