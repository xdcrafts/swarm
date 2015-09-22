package org.swarm.commons;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Function utils.
 */
public final class SupplierUtils {

    private SupplierUtils() {
        // Nothing
    }

    /**
     * Returns function that transforms supplier<T> to supplier<U> by application of mapper function.
     */
    public static <T, U> Function<Supplier<T>, Supplier<U>> supply(Function<T, U> mapper) {
        return supplier -> () -> mapper.apply(supplier.get());
    }

    /**
     * Returns function that transforms supplier<T> to supplier<U> by application of mapper function.
     */
    public static <T, U> Function<Supplier<T>, Supplier<U>> supplyWith(Function<T, Supplier<U>> mapper) {
        return supplier -> mapper.apply(supplier.get());
    }

    /**
     * Returns function that transforms supplier.
     */
    public static <T> Function<Supplier<T>, Supplier<Optional<T>>> supply(Predicate<T> filter) {
        return supplier -> {
            final T value = supplier.get();
            return filter.test(value) ? () -> Optional.ofNullable(value) : Optional::empty;
        };
    }
}
