package com.github.xdcrafts.swarm.commons;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

    private static final Predicate<Optional<?>> IS_ABSENT = o -> !o.isPresent();

    /**
     * Applies runnable if optional is not present.
     */
    public static void ifNotPresent(Optional<?> optional, Runnable runnable) {
        if (!optional.isPresent()) {
            runnable.run();
        }
    }

    /**
     * If first optional is present then returns first, in other case return second.
     */
    public static <T> Optional<T> or(Optional<T> first, Optional<T> second) {
        return first.isPresent() ? first : second;
    }

    /**
     * If first optional is present then returns first, in other case runs supplier, returning second.
     * This overload doesn't require calculation of second if the first is present.
     */
    public static <T> Optional<T> or(Optional<T> first, Supplier<Optional<T>> second) {
        return first.isPresent() ? first : second.get();
    }

    /**
     * If both optionals is present then applies biFunction
     * to their contents, or returns empty otherwise.
     */
    public static <T1, T2, U> Optional<U> yieldFor(
            Optional<T1> t1Optional, Optional<T2> t2Optional, BiFunction<T1, T2, U> biFunction
    ) {
        final Optional<U> uOptional;
        if (t1Optional.isPresent() && t2Optional.isPresent()) {
            uOptional = Optional.ofNullable(biFunction.apply(t1Optional.get(), t2Optional.get()));
        } else {
            uOptional = Optional.empty();
        }
        return uOptional;
    }

    /**
     * If both optionals is present then applies biConsumer to their contents.
     */
    public static <T1, T2> void applyFor(
            Optional<T1> t1Optional, Optional<T2> t2Optional, BiConsumer<T1, T2> biConsumer
    ) {
        if (t1Optional.isPresent() && t2Optional.isPresent()) {
            biConsumer.accept(t1Optional.get(), t2Optional.get());
        }
    }

    /**
     * Applies runnable if all optionals are present.
     */
    public static void applyForAll(Optional<?>[] optionals, Runnable supplier) {
        final boolean isAnyAbsent = Arrays.asList(optionals).stream().filter(IS_ABSENT).findAny().isPresent();
        if (!isAnyAbsent) {
            supplier.run();
        }
    }

    /**
     * Applies runnable if at least one optional is present.
     */
    public static void applyForAny(Optional<?>[] optionals, Runnable supplier) {
        final boolean isAnyExists = Arrays.asList(optionals).stream().filter(Optional::isPresent).findAny().isPresent();
        if (isAnyExists) {
            supplier.run();
        }
    }

    /**
     * Applies supplier if all optionals are present.
     */
    public static <T> Optional<T> supplyForAll(Optional<?>[] optionals, Supplier<T> supplier) {
        final boolean isAnyAbsent = Arrays.asList(optionals).stream().filter(IS_ABSENT).findAny().isPresent();
        return isAnyAbsent ? Optional.<T>empty() : Optional.ofNullable(supplier.get());
    }

    /**
     * Applies supplier if at least one optional is present.
     */
    public static <T> Optional<T> supplyForAny(Optional<?>[] optionals, Supplier<T> supplier) {
        final boolean isAnyExists = Arrays.asList(optionals).stream().filter(Optional::isPresent).findAny().isPresent();
        return isAnyExists ? Optional.ofNullable(supplier.get()) : Optional.<T>empty();
    }

    /**
     * Calls ifEmpty if optional empty or ifPresent if optional is present.
     */
    public static <T> void handle(Optional<T> optional, Runnable ifEmpty, Consumer<T> ifPresent) {
        if (optional.isPresent()) {
            ifPresent.accept(optional.get());
        } else {
            ifEmpty.run();
        }
    }

    /**
     * value.toString or 'empty'.
     */
    public static String asString(Optional<?> optional) {
        return optional.isPresent() ? optional.get().toString() : "empty";
    }
}
