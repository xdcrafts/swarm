package com.github.xdcrafts.swarm.util.function.extra.carried;

import com.github.xdcrafts.swarm.util.function.IFunction;
import com.github.xdcrafts.swarm.util.function.extra.IFunction2;

/**
 * Carried function.
 * @param <A1> first argument type
 * @param <A2> second argument type
 * @param <B> return type
 */
public final class Carried2<A1, A2, B> implements IFunction<A2, B> {
    private final A1 a1;
    private final IFunction2<A1, A2, B> function2;
    /**
     * Carries function.
     * @param a1 first argument
     * @param function2 function
     * @param <A1> first argument type
     * @param <A2> second argument type
     * @param <B> return type
     * @return carried function
     */
    public static <A1, A2, B> IFunction<A2, B> carry(A1 a1, IFunction2<A1, A2, B> function2) {
        return new Carried2<>(a1, function2);
    }
    private Carried2(A1 a1, IFunction2<A1, A2, B> function2) {
        this.a1 = a1;
        this.function2 = function2;
    }
    @Override public B apply(A2 a2) {
        return this.function2.apply(a1, a2);
    }
}
