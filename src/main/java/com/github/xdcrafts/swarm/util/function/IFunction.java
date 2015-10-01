package com.github.xdcrafts.swarm.util.function;

import com.github.xdcrafts.swarm.util.function.extra.carried.Carry;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Function of one argument.
 * @param <A> argument type
 * @param <B> return type
 */
public interface IFunction<A, B> extends Function<A, B> {
    /**
     * Carries function.
     * @param a argument.
     * @return carried function
     */
    default ISupplier<B> carry(A a) {
        return Carry.carry(a, this);
    }
    /**
     * Ommits return value.
     * @return consumer of values of type A
     */
    default Consumer<A> omit() {
        return this::apply;
    }
}
