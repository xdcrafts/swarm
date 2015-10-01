package com.github.xdcrafts.swarm.util;

import com.github.xdcrafts.swarm.util.function.ISupplier;

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
     * Lazy value. Supplier is executed only once.
     * @param <T> value type
     * @param supplier value that needs to be lazily
     * @return new supplier that calculates value only once
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
     * @param <T> value type
     * @param supplier value that needs to be strict
     * @return new supplier that always returns constant precalculated value
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
     * @param <T> value type
     * @param value just plain value
     * @return returns value wrapped with supplier
     */
    public static <T> ISupplier<T> supply(T value) {
        return () -> value;
    }

    /**
     * Returns function that transforms supplier T to supplier U by application of mapper function.
     * @param <T> origin value type
     * @param <U> new value type
     * @param mapper function that transforms T to U
     * @return function that transforms suppliers
     */
    public static <T, U> Function<Supplier<T>, ISupplier<U>> mapSupply(Function<T, U> mapper) {
        return supplier -> () -> mapper.apply(supplier.get());
    }

    /**
     * Returns function that transforms supplier T to supplier U by application of mapper function.
     * @param <T> origin value type
     * @param <U> new value type
     * @param mapper function that transforms T to Supplier U
     * @return function that transforms suppliers
     */
    public static <T, U> Function<Supplier<T>, ISupplier<U>> flatMapSupply(Function<T, ISupplier<U>> mapper) {
        return supplier -> mapper.apply(supplier.get());
    }

    /**
     * Returns function that transforms supplier.
     * @param <T> value type
     * @param filter predicate for values of type T
     * @return function that transforms suppliers
     */
    public static <T> Function<Supplier<T>, ISupplier<Optional<T>>> filterSupply(Predicate<T> filter) {
        return supplier -> {
            final T value = supplier.get();
            return filter.test(value) ? () -> Optional.ofNullable(value) : Optional::empty;
        };
    }
}
