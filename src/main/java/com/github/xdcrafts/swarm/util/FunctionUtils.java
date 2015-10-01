package com.github.xdcrafts.swarm.util;

import com.github.xdcrafts.swarm.util.function.IThrowingConsumer;
import com.github.xdcrafts.swarm.util.function.IThrowingFunction;
import com.github.xdcrafts.swarm.util.function.IThrowingSupplier;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * IFunction utility functions.
 */
public class FunctionUtils {
    /**
     * Converts simple function to function that may throw an exception.
     * @param function simple function
     * @param <A> argument type
     * @param <B> return type
     * @return throwing function
     */
    public static <A, B> IThrowingFunction<A, B> asTf(final Function<A, B> function) {
        return function::apply;
    }
    /**
     * Converts simple function to function that may throw an exception.
     * @param function supplier
     * @param <B> supplier value type
     * @return throwing supplier
     */
    public static <B> IThrowingSupplier<B> asTs(final Supplier<B> function) {
        return function::get;
    }
    /**
     * Converts simple function to function that may throw an exception.
     * @param function consumer function
     * @param <A> consumed value type
     * @return throwing consumer
     */
    public static <A> IThrowingConsumer<A> asTc(final Consumer<A> function) {
        return function::accept;
    }
}
