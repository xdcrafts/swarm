/*******************************************************************************
 * Copyright (C) PlaceIQ, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by placeiq 2015
 *******************************************************************************/
package com.github.xdcrafts.swarm.commons;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
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

    /**
     * Creates supplier from simple value.
     */
    public static <T> ISupplier<T> supply(T value) {
        return () -> value;
    }

    /**
     * Returns function that transforms supplier<T> to supplier<U> by application of mapper function.
     */
    public static <T, U> Function<Supplier<T>, ISupplier<U>> mapSupply(Function<T, U> mapper) {
        return supplier -> () -> mapper.apply(supplier.get());
    }

    /**
     * Returns function that transforms supplier<T> to supplier<U> by application of mapper function.
     */
    public static <T, U> Function<Supplier<T>, ISupplier<U>> flatMapSupply(Function<T, ISupplier<U>> mapper) {
        return supplier -> mapper.apply(supplier.get());
    }

    /**
     * Returns function that transforms supplier.
     */
    public static <T> Function<Supplier<T>, ISupplier<Optional<T>>> filterSupply(Predicate<T> filter) {
        return supplier -> {
            final T value = supplier.get();
            return filter.test(value) ? () -> Optional.ofNullable(value) : Optional::empty;
        };
    }
}
