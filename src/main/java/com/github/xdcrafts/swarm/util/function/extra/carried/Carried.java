package com.github.xdcrafts.swarm.util.function.extra.carried;

import com.github.xdcrafts.swarm.util.function.IFunction;
import com.github.xdcrafts.swarm.util.function.ISupplier;

/**
 * Carried function.
 * @param <A> first argument
 * @param <B> return type
 */
public final class Carried<A, B> implements ISupplier<B> {
    private final A a;
    private final IFunction<A, B> function;
    /**
     * Carries function.
     * @param a first argument
     * @param function function
     * @param <A> type of first argument
     * @param <B> return type
     * @return supplier of type B
     */
    public static <A, B> ISupplier<B> carry(A a, IFunction<A, B> function) {
        return new Carried<>(a, function);
    }
    private Carried(A a, IFunction<A, B> function) {
        this.a = a;
        this.function = function;
    }
    @Override public B get() {
        return this.function.apply(this.a);
    }
}
